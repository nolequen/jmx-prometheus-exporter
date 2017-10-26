package su.nlq.prometheus.jmx.connection.remote.jmxmp;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.remote.Connector;
import su.nlq.prometheus.jmx.connection.remote.RemoteConnectionConfiguration;

import javax.xml.bind.annotation.XmlType;
import java.util.Optional;

@XmlType(name = "jmxmp-connection")
public final class JMXMPConnectionConfiguration extends RemoteConnectionConfiguration {

  @Override
  protected @NotNull Connector connector(@NotNull String address, @NotNull Optional<String[]> credentials) {
    return new JMXMPConnector(address, credentials);
  }
}
