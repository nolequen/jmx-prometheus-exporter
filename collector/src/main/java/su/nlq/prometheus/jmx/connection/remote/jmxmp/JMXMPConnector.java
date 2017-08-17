package su.nlq.prometheus.jmx.connection.remote.jmxmp;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.remote.Connector;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Optional;

public final class JMXMPConnector extends Connector {

  public JMXMPConnector(@NotNull String address, @NotNull Optional<String[]> credentials) {
    super(address, credentials);
  }

  @Override
  public @NotNull JMXConnector connect() throws IOException {
    final JMXServiceURL address = new JMXServiceURL("service:jmx:jmxmp://" + address());
    final javax.management.remote.jmxmp.JMXMPConnector connector = new javax.management.remote.jmxmp.JMXMPConnector(address, environment());
    connector.connect();
    return connector;
  }
}
