package su.nlq.prometheus.jmx.connection.remote;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.Connection;
import su.nlq.prometheus.jmx.logging.Logger;

import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.util.function.Consumer;

public final class RemoteConnection implements Connection {
  private final @NotNull Connector connector;

  public RemoteConnection(@NotNull Connector connector) {
    this.connector = connector;
  }

  @Override
  public void accept(@NotNull Consumer<MBeanServerConnection> consumer) {
    connector.accept(connection -> {
      try {
        consumer.accept(connection.getMBeanServerConnection());
      } catch (IOException e) {
        Logger.instance.error("Can't get remote mbean server connection", e);
      }
    });
  }
}
