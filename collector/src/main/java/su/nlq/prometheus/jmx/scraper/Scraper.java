package su.nlq.prometheus.jmx.scraper;

import org.jetbrains.annotations.NotNull;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;

public final class Scraper {
  private final @NotNull MBeanServerConnection connection;

  @SuppressWarnings("StaticMethodNamingConvention")
  public static @NotNull Scraper of(@NotNull MBeanServerConnection connection) {
    return new Scraper(connection);
  }

  private Scraper(@NotNull MBeanServerConnection connection) {
    this.connection = connection;
  }

  public @NotNull Receiver.Consumer scrape(@NotNull Iterable<ObjectInstance> beans) {
    return receiver -> {
      final BeanScraper scraper = new BeanScraper(connection, receiver);
      beans.forEach(bean -> scraper.scrape(bean.getObjectName()));
    };
  }
}
