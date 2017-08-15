package su.nlq.prometheus.jmx.connection.remote;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.logging.Logger;

import javax.management.remote.JMXConnector;
import java.io.IOException;
import java.util.Optional;
import java.util.function.BiConsumer;

public final class CachedConnector implements Connector {
  private @NotNull Optional<JMXConnector> connector;
  private @NotNull String address;

  public CachedConnector(@NotNull ConnectorSupplier supplier) {
    address = supplier.address();
    try {
      connector = Optional.of(supplier.get());
    } catch (IOException e) {
      Logger.instance.error("Error during connector creation", e);
      connector = Optional.empty();
    }
  }

  @Override
  public void accept(@NotNull BiConsumer<String, JMXConnector> consumer) {
    connector.ifPresent(jmxConnector -> consumer.accept(address, jmxConnector));
  }
}
