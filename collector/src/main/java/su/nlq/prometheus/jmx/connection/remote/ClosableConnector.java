package su.nlq.prometheus.jmx.connection.remote;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.logging.Logger;

import javax.management.remote.JMXConnector;
import java.io.IOException;
import java.util.function.Consumer;

public final class ClosableConnector implements Connector {
  private final @NotNull ConnectorSupplier supplier;

  public ClosableConnector(@NotNull ConnectorSupplier supplier) {
    this.supplier = supplier;
  }

  @Override
  public void accept(@NotNull Consumer<JMXConnector> consumer) {
    try (final JMXConnector connector = supplier.get()) {
      consumer.accept(connector);
    } catch (IOException e) {
      Logger.instance.error("Error during connector creation", e);
    }
  }
}
