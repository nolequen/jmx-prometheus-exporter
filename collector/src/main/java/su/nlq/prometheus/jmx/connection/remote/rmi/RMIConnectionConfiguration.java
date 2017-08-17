package su.nlq.prometheus.jmx.connection.remote.rmi;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.ConnectionConfiguration;
import su.nlq.prometheus.jmx.connection.remote.Connector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.Optional;

@XmlType(name = "rmi-connection")
public final class RMIConnectionConfiguration extends ConnectionConfiguration {
  @XmlAttribute(required = false)
  private @NotNull Boolean ssl = false;

  @Override
  protected @NotNull Connector connector(@NotNull String address, @NotNull Optional<String[]> credentials) {
    return new RMIConnector(address, credentials, ssl);
  }
}
