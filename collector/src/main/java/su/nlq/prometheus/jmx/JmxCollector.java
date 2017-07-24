package su.nlq.prometheus.jmx;

import io.prometheus.client.Collector;
import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.Connection;
import su.nlq.prometheus.jmx.interpreter.Interpreter;
import su.nlq.prometheus.jmx.interpreter.MBean;
import su.nlq.prometheus.jmx.logging.Logger;
import su.nlq.prometheus.jmx.scraper.Receiver;
import su.nlq.prometheus.jmx.scraper.Scraper;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class JmxCollector extends Collector {
  private final @NotNull Iterable<Connection> connections;
  private final @NotNull MBeansCollector mbeans;
  private final @NotNull Interpreter interpreter;

  public static void register(@NotNull File config) throws IOException {
    try {
      Configuration.parse(config).map(JmxCollector::new).ifPresent(Collector::register);
    } catch (@SuppressWarnings("OverlyBroadCatchBlock") IOException | JAXBException e) {
      throw new IOException("Failed to parse " + config, e);
    }
  }

  private JmxCollector(@NotNull Configuration configuration) {
    connections = configuration.getConnections();
    mbeans = configuration.getMBeansCollector();
    interpreter = configuration.getInterpreter();
  }

  @Override
  public @NotNull List<MetricFamilySamples> collect() {
    final MeasuringReceiver receiver = MeasuringReceiver.start(new DefaultReceiver(interpreter));
    connections.forEach(connection -> connection.accept(serverConnection -> Scraper.of(serverConnection).scrape(mbeans.collect(serverConnection)).to(receiver)));
    return receiver.stop();
  }

  private static final class MeasuringReceiver implements Receiver {
    private static final @NotNull String METRICS_NAME = "jmx_scrape_duration_ms";
    private static final @NotNull String HELP = "Scrape duration in milliseconds.";

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
      final long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
      final List<MetricFamilySamples> samples = receiver.getSamples();
      samples.add(new MetricFamilySamples(METRICS_NAME, Type.GAUGE, HELP, Collections.singletonList(
          new MetricFamilySamples.Sample(METRICS_NAME, Collections.emptyList(), Collections.emptyList(), duration))));
      return samples;
    }
  }

  private static final class DefaultReceiver implements Receiver {
    private final @NotNull Interpreter interpreter;
    private final @NotNull Map<String, MetricFamilySamples> samples = new HashMap<>();

    public DefaultReceiver(@NotNull Interpreter interpreter) {
      this.interpreter = interpreter;
    }

    @Override
    public void accept(@NotNull MBean bean, @NotNull Object value) {
      interpreter.accept(bean, value).accept((sample, type, help) -> {
        Logger.instance.trace("Adding metric sample {}: {}", sample.name, sample.value);
        samples
            .computeIfAbsent(sample.name, name -> new MetricFamilySamples(name, type, help, new ArrayList<>()))
            .samples.add(sample);
      });
    }

    public @NotNull List<MetricFamilySamples> getSamples() {
      return new ArrayList<>(samples.values());
    }
  }
}
