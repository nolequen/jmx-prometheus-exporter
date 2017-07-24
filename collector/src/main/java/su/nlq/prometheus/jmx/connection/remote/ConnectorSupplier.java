package su.nlq.prometheus.jmx.connection.remote;

import org.jetbrains.annotations.NotNull;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class ConnectorSupplier {
  private final @NotNull String url;
  private final @NotNull Optional<String[]> credentials;

  protected ConnectorSupplier(@NotNull String url, @NotNull Optional<String[]> credentials) {
    this.url = url;
    this.credentials = credentials;
  }

  public abstract @NotNull JMXConnector get() throws IOException;

  protected final @NotNull Map<String, Object> getEnvironment() {
    final Map<String, Object> environment = new HashMap<>();
    credentials.ifPresent(value -> environment.put(JMXConnector.CREDENTIALS, value));
    return environment;
  }

  protected final @NotNull JMXServiceURL getURL() throws MalformedURLException {
    return new JMXServiceURL(url);
  }
}
