package su.nlq.prometheus.jmx.connection;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.logging.Logger;

import java.io.IOException;

public interface Connection {

  static void fail(@NotNull String address, @NotNull IOException e) {
    Logger.instance.error("Failed connect to " + address + ": " + e.getMessage());
  }

  void accept(@NotNull ConnectionConsumer consumer);
}
