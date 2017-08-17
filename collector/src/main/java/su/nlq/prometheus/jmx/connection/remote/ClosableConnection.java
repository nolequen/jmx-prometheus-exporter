package su.nlq.prometheus.jmx.connection.remote;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.Connection;
import su.nlq.prometheus.jmx.connection.ConnectionConsumer;

import javax.management.remote.JMXConnector;
import java.io.IOException;

public final class ClosableConnection implements Connection {
  private final @NotNull Connector supplier;

  public ClosableConnection(@NotNull Connector supplier) {
    this.supplier = supplier;
  }

  @Override
  public void accept(@NotNull ConnectionConsumer consumer) {
    try (final JMXConnector connector = supplier.connect()) {
      consumer.accept(supplier.address(), connector.getMBeanServerConnection());
    } catch (IOException e) {
      Connection.fail(supplier.address(), e);
    }
  }
}
