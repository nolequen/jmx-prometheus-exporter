package su.nlq.prometheus.jmx.connection.local;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.Connection;
import su.nlq.prometheus.jmx.connection.ConnectionConsumer;

import java.lang.management.ManagementFactory;
import java.util.Optional;

public final class LocalConnection extends Connection {

  public LocalConnection(@NotNull Optional<String> name) {
    super(name);
  }

  public LocalConnection() {
    super(Optional.empty());
  }

  @Override
  public void accept(@NotNull ConnectionConsumer consumer) {
    consumer.accept(name(ManagementFactory.getRuntimeMXBean().getName()), ManagementFactory.getPlatformMBeanServer());
  }
}
