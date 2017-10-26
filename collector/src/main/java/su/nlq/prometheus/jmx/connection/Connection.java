package su.nlq.prometheus.jmx.connection;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.logging.Logger;

import java.io.IOException;
import java.util.Optional;

public abstract class Connection {
  private final @NotNull Optional<String> name;

  protected Connection(@NotNull Optional<String> name) {
    this.name = name;
  }

  public abstract void accept(@NotNull ConnectionConsumer consumer);

  protected final @NotNull String name(@NotNull String other) {
    return name.orElse(other);
  }

  protected static void fail(@NotNull String name, @NotNull IOException e) {
    Logger.instance.error("Failed connect to " + name + ": " + e.getMessage());
  }
}
