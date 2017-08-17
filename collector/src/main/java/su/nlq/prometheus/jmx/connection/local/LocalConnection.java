package su.nlq.prometheus.jmx.connection.local;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.Connection;
import su.nlq.prometheus.jmx.connection.ConnectionConsumer;

import java.lang.management.ManagementFactory;

public final class LocalConnection implements Connection {

  @Override
  public void accept(@NotNull ConnectionConsumer consumer) {
    consumer.accept(ManagementFactory.getRuntimeMXBean().getName(), ManagementFactory.getPlatformMBeanServer());
  }
}
