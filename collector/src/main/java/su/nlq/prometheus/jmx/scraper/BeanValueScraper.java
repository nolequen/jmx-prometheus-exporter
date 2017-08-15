package su.nlq.prometheus.jmx.scraper;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.interpreter.MBean;
import su.nlq.prometheus.jmx.logging.Logger;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularDataSupport;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

enum BeanValueScraper {

  Incompatible(ObjectName.class, String.class, String[].class, long[].class, Boolean.class) {
    @Override
    protected void process(@NotNull Receiver receiver, @NotNull MBean bean, @NotNull Object value) {
      Logger.instance.trace("Skipping incompatible data {}: {}", bean, value);
    }
  },

  SimpleData(Number.class) {
    @Override
    protected void process(@NotNull Receiver receiver, @NotNull MBean bean, @NotNull Object value) {
      Logger.instance.trace("Processing {}", bean);
      receiver.accept(bean, value);
    }
  },

  CompositeData(CompositeData.class) {
    @Override
    protected void process(@NotNull Receiver receiver, @NotNull MBean bean, @NotNull Object value) {
      Logger.instance.trace("Processing composite data: {}", bean);
      final CompositeData composite = (CompositeData) value;
      composite.getCompositeType().keySet().forEach(key -> scrape(receiver, bean.compose(key), composite.get(key)));
    }
  },

  CompositeDataArray(CompositeData[].class) {
    @Override
    protected void process(@NotNull Receiver receiver, @NotNull MBean bean, @NotNull Object value) {
      Logger.instance.trace("Processing composite data array: {}", bean);
      for (CompositeData element : (CompositeData[]) value) {
        CompositeData.process(receiver, bean, element);
      }
    }
  },

  TabularData(TabularDataSupport.class) {
    @Override
    protected void process(@NotNull Receiver receiver, @NotNull MBean bean, @NotNull Object value) {
      Logger.instance.trace("Processing tabular data: {}", bean);
      for (Map.Entry<Object, Object> entry : ((TabularDataSupport) value).entrySet()) {
        final Object object = entry.getValue();
        if (object instanceof CompositeData) {
          final CompositeData composite = (CompositeData) object;
          composite.getCompositeType().keySet().forEach(key -> scrape(receiver, bean.labeled(key, entry.getKey().toString()), composite.get(key)));
        } else {
          Logger.instance.debug("{} has invalid tabular data format", bean.getName());
        }
      }
    }
  };

  private static final @NotNull Collection<BeanValueScraper> scrapers = Arrays.asList(values());

  private final @NotNull Collection<Class<?>> classes;

  public static void scrape(@NotNull Receiver receiver, @NotNull MBean bean, @NotNull Object value) {
    if (scrapers.stream().noneMatch(scraper -> scraper.tryScrape(receiver, bean, value))) {
      Logger.instance.debug("Unknown data {} found: {}", bean, value);
    }
  }

  private BeanValueScraper(@NotNull Class<?>... classes) {
    this.classes = Arrays.asList(classes);
  }

  protected boolean tryScrape(@NotNull Receiver receiver, @NotNull MBean bean, @NotNull Object value) {
    return classes.stream()
        .filter(supported -> supported.isInstance(value))
        .findAny()
        .map(supported -> {
          process(receiver, bean, value);
          return true;
        }).orElse(false);
  }

  protected abstract void process(@NotNull Receiver receiver, @NotNull MBean bean, @NotNull Object value);
}
