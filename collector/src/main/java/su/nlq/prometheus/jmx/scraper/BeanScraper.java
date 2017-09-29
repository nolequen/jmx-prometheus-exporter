package su.nlq.prometheus.jmx.scraper;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.bean.MBean;
import su.nlq.prometheus.jmx.logging.Logger;

import javax.management.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

final class BeanScraper {

  private final @NotNull MBeanServerConnection connection;
  private final @NotNull Receiver receiver;

  public BeanScraper(@NotNull MBeanServerConnection connection, @NotNull Receiver receiver) {
    this.connection = connection;
    this.receiver = receiver;
  }

  public void scrape(@NotNull ObjectName name) {
    getMBeanAttributesInfo(name).ifPresent(infos -> infos.forEach(info ->
        getAttributeValue(name, info).ifPresent(value -> {
          Logger.instance.trace("Scraping '{}[{}]'", name, info);
          MBean.of(name, info).ifPresent(bean -> BeanValueScraper.scrape(receiver, bean, value));
        })));
  }

  private @NotNull Optional<Object> getAttributeValue(@NotNull ObjectName bean, @NotNull MBeanAttributeInfo info) {
    if (!info.isReadable()) {
      Logger.instance.trace("Attribute '{}[{}]' can't be read", bean, info);
      return Optional.empty();
    }
    try {
      return Optional.ofNullable(connection.getAttribute(bean, info.getName()));
    } catch (IOException | InstanceNotFoundException | ReflectionException | MBeanException | AttributeNotFoundException | RuntimeMBeanException e) {
      Logger.instance.debug("Failed to get attribute '" + bean + '[' + info + "]'", e);
      return Optional.empty();
    }
  }

  private @NotNull Optional<Iterable<MBeanAttributeInfo>> getMBeanAttributesInfo(@NotNull ObjectName name) {
    try {
      return Optional.of(Arrays.asList(connection.getMBeanInfo(name).getAttributes()));
    } catch (IOException | InstanceNotFoundException | IntrospectionException | ReflectionException e) {
      Logger.instance.debug("Failed to get attributes for " + name, e);
      return Optional.empty();
    }
  }
}
