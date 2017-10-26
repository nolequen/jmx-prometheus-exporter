package su.nlq.prometheus.jmx.connection.remote;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nlq.prometheus.jmx.connection.Connection;
import su.nlq.prometheus.jmx.connection.ConnectionConsumer;

import javax.management.remote.JMXConnector;
import java.io.IOException;
import java.util.Optional;

public final class CachedConnection extends Connection {
  private final @NotNull Connector supplier;
  private @Nullable JMXConnector connector;

  public CachedConnection(@NotNull Optional<String> name, @NotNull Connector supplier) {
    super(name);
    this.supplier = supplier;
    this.connector = connect(supplier);
  }

  @Override
  public void accept(@NotNull ConnectionConsumer consumer) {
    if (connector == null) {
      connector = connect(supplier);
      return;
    }
    try {
      consumer.accept(name(supplier.address()), connector.getMBeanServerConnection());
    } catch (IOException e) {
      Connection.fail(supplier.address(), e);
      connector = null;
    }
  }

  private static @Nullable JMXConnector connect(@NotNull Connector supplier) {
    try {
      return supplier.connect();
    } catch (IOException e) {
      Connection.fail(supplier.address(), e);
      return null;
    }
  }
}
