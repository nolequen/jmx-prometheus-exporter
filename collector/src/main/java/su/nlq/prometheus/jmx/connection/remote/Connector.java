package su.nlq.prometheus.jmx.connection.remote;

import org.jetbrains.annotations.NotNull;

import javax.management.remote.JMXConnector;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Connector {
  private final @NotNull String address;
  private final @NotNull Optional<String[]> credentials;

  protected Connector(@NotNull String address, @NotNull Optional<String[]> credentials) {
    this.address = address;
    this.credentials = credentials;
  }

  public abstract @NotNull JMXConnector connect() throws IOException;

  public final @NotNull String address() {
    return address;
  }

  protected final @NotNull Map<String, Object> environment() {
    final Map<String, Object> environment = new HashMap<>();
    credentials.ifPresent(value -> environment.put(JMXConnector.CREDENTIALS, value));
    return environment;
  }
}
