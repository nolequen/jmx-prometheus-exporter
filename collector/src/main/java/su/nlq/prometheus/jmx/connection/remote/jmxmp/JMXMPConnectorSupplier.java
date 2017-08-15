package su.nlq.prometheus.jmx.connection.remote.jmxmp;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.remote.ConnectorSupplier;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.jmxmp.JMXMPConnector;
import java.io.IOException;
import java.util.Optional;

public final class JMXMPConnectorSupplier extends ConnectorSupplier {

  public JMXMPConnectorSupplier(@NotNull String address, @NotNull Optional<String[]> credentials) {
    super(address, credentials);
  }

  @Override
  public @NotNull JMXConnector get() throws IOException {
    final JMXServiceURL address = new JMXServiceURL("service:jmx:jmxmp://" + address());
    final JMXMPConnector connector = new JMXMPConnector(address, environment());
    connector.connect();
    return connector;
  }
}
