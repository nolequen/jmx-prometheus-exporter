package su.nlq.prometheus.jmx.connection.local;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.Connection;

import javax.management.MBeanServerConnection;
import java.lang.management.ManagementFactory;
import java.util.function.BiConsumer;

public final class LocalConnection implements Connection {

  @Override
  public void accept(@NotNull BiConsumer<String, MBeanServerConnection> consumer) {
    consumer.accept(ManagementFactory.getRuntimeMXBean().getName(), ManagementFactory.getPlatformMBeanServer());
  }
}
