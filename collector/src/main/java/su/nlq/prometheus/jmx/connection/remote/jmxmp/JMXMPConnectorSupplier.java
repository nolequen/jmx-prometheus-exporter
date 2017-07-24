package su.nlq.prometheus.jmx.connection.remote.jmxmp;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.remote.ConnectorSupplier;

import javax.management.remote.JMXConnector;
import javax.management.remote.jmxmp.JMXMPConnector;
import java.io.IOException;
import java.util.Optional;

public final class JMXMPConnectorSupplier extends ConnectorSupplier {

  public JMXMPConnectorSupplier(@NotNull String address, @NotNull Optional<String[]> credentials) {
    super("service:jmx:jmxmp://" + address, credentials);
  }

  @Override
  public @NotNull JMXConnector get() throws IOException {
    final JMXMPConnector connector = new JMXMPConnector(getURL(), getEnvironment());
    connector.connect();
    return connector;
  }
}
