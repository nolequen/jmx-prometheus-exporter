package su.nlq.prometheus.jmx.connection.remote.jmxmp;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.ConnectionConfiguration;
import su.nlq.prometheus.jmx.connection.remote.ConnectorSupplier;

import javax.xml.bind.annotation.XmlType;
import java.util.Optional;

@XmlType(name = "jmxmp-connection")
public final class JMXMPConnectionConfiguration extends ConnectionConfiguration {

  @Override
  protected @NotNull ConnectorSupplier connector(@NotNull String address, @NotNull Optional<String[]> credentials) {
    return new JMXMPConnectorSupplier(address, credentials);
  }
}
