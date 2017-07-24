package su.nlq.prometheus.jmx.connection.remote;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.logging.Logger;

import javax.management.remote.JMXConnector;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

public final class CachedConnector implements Connector {

  private @NotNull Optional<JMXConnector> connector;

  public CachedConnector(@NotNull ConnectorSupplier supplier) {
    try {
      connector = Optional.of(supplier.get());
    } catch (IOException e) {
      Logger.instance.error("Error during connector creation", e);
      connector = Optional.empty();
    }
  }

  @Override
  public void accept(@NotNull Consumer<JMXConnector> consumer) {
    connector.ifPresent(consumer);
  }
}
