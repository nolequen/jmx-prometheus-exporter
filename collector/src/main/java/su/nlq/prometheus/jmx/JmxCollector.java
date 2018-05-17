package su.nlq.prometheus.jmx;

import io.prometheus.client.Collector;
import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.bean.Labels;
import su.nlq.prometheus.jmx.bean.MBean;
import su.nlq.prometheus.jmx.connection.Connection;
import su.nlq.prometheus.jmx.logging.Logger;
import su.nlq.prometheus.jmx.scraper.Receiver;
import su.nlq.prometheus.jmx.scraper.Scraper;

import java.util.*;

public final class JmxCollector extends Collector {
  private final @NotNull Iterable<Connection> connections;
  private final @NotNull MBeansCollector mbeans;

  public static @NotNull Collector register(@NotNull Connection connection) {
    return register(Collections.singleton(connection), Collections.emptyList(), Collections.emptyList());
  }

  public static @NotNull Collector register(@NotNull Iterable<Connection> connections, @NotNull List<String> whitelist, @NotNull List<String> blacklist) {
    return new JmxCollector(connections, MBeansCollector.create(whitelist, blacklist)).register();
  }

  private JmxCollector(@NotNull Iterable<Connection> connections, @NotNull MBeansCollector collector) {
    this.connections = connections;
    this.mbeans = collector;
  }

  @Override
  public @NotNull List<MetricFamilySamples> collect() {
    final MeasuringReceiver receiver = MeasuringReceiver.start(new DefaultReceiver());
    connections.forEach(connection ->
        connection.accept((connectionName, serverConnection) ->
            Scraper.of(serverConnection)
                .scrape(mbeans.collect(serverConnection))
                .to(new ConnectionReceiver(receiver, connectionName))));
    return receiver.stop();
  }

  private static final class ConnectionReceiver implements Receiver {
    private final @NotNull Receiver receiver;
    private final @NotNull String connection;

    public ConnectionReceiver(@NotNull Receiver receiver, @NotNull String connection) {
      this.receiver = receiver;
      this.connection = connection;
    }

    @Override
    public void accept(@NotNull MBean bean, @NotNull Object value) {
      receiver.accept(bean.labeled("connection", connection), value);
    }
  }

  private static final class MeasuringReceiver implements Receiver {
    private static final @NotNull String METRICS_NAME = "jmx_scrape_duration";
    private static final @NotNull String HELP = "Scrape duration in nanoseconds.";

    private final @NotNull DefaultReceiver receiver;
    private final long startTime = System.nanoTime();

    public static @NotNull MeasuringReceiver start(@NotNull DefaultReceiver receiver) {
      return new MeasuringReceiver(receiver);
    }

    private MeasuringReceiver(@NotNull DefaultReceiver receiver) {
      this.receiver = receiver;
    }

    @Override
    public void accept(@NotNull MBean bean, @NotNull Object value) {
      receiver.accept(bean, value);
    }

    public @NotNull List<MetricFamilySamples> stop() {
      final long duration = System.nanoTime() - startTime;
      final List<MetricFamilySamples> samples = receiver.samples();
      samples.add(new MetricFamilySamples(METRICS_NAME, Type.GAUGE, HELP, Collections.singletonList(
          new MetricFamilySamples.Sample(METRICS_NAME, Collections.emptyList(), Collections.emptyList(), duration))));
      return samples;
    }
  }

  private static final class DefaultReceiver implements Receiver {
    private final @NotNull Map<String, MetricFamilySamples> samples = new HashMap<>();

    @Override
    public void accept(@NotNull MBean bean, @NotNull Object value) {
      if (!(value instanceof Number)) {
        return;
      }
      final String name = bean.getName();
      if (name.isEmpty()) {
        return;
      }
      final String help = bean.getHelp();
      if (help.isEmpty()) {
        return;
      }
      Logger.instance.trace("Adding metric sample {}: {}", name, value);
      final Labels labels = bean.getLabels();
      samples
          .computeIfAbsent(name, k -> new MetricFamilySamples(k, Type.GAUGE, help, new ArrayList<>()))
          .samples.add(new MetricFamilySamples.Sample(name, labels.getNames(), labels.getValues(), ((Number) value).doubleValue()));
    }

    public @NotNull List<MetricFamilySamples> samples() {
      return new ArrayList<>(samples.values());
    }
  }
}
