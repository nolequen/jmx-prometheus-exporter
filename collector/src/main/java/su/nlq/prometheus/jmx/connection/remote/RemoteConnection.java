package su.nlq.prometheus.jmx.connection.remote;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.Connection;
import su.nlq.prometheus.jmx.connection.ConnectionConsumer;

public final class RemoteConnection implements Connection {
  private final @NotNull Connection connection;

  public RemoteConnection(@NotNull Connection connection) {
    this.connection = connection;
  }

  @Override
  public void accept(@NotNull ConnectionConsumer consumer) {
    connection.accept(consumer);
  }
}
