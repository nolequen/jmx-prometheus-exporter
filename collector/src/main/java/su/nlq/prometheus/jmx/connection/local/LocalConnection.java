package su.nlq.prometheus.jmx.connection.local;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.Connection;

import javax.management.MBeanServerConnection;
import java.lang.management.ManagementFactory;
import java.util.function.Consumer;

public final class LocalConnection implements Connection {

  @Override
  public void accept(@NotNull Consumer<MBeanServerConnection> consumer) {
    consumer.accept(ManagementFactory.getPlatformMBeanServer());
  }
}
