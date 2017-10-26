package su.nlq.prometheus.jmx.connection.remote;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.Connection;
import su.nlq.prometheus.jmx.connection.ConnectionConsumer;

import java.util.Optional;

public final class RemoteConnection extends Connection {
  private final @NotNull Connection connection;

  public RemoteConnection(@NotNull Optional<String> name, @NotNull Connection connection) {
    super(name);
    this.connection = connection;
  }

  @Override
  public void accept(@NotNull ConnectionConsumer consumer) {
    connection.accept(consumer);
  }
}
