package su.nlq.prometheus.jmx.connection.remote;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.Connection;
import su.nlq.prometheus.jmx.connection.ConnectionConfiguration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Optional;

@XmlType(name = "remote-connection")
public abstract class RemoteConnectionConfiguration extends ConnectionConfiguration {
  @XmlAttribute(required = true)
  private @NotNull String address = "";
  @XmlAttribute
  private boolean keep = false;
  @XmlElement
  private @NotNull String username = "";
  @XmlElement
  private @NotNull String password = "";

  @Override
  public final @NotNull Connection get() {
    final Connector connector = connector(address, credentials());
    return new RemoteConnection(name(), keep ? new CachedConnection(name(), connector) : new ClosableConnection(name(), connector));
  }

  protected abstract @NotNull Connector connector(@NotNull String connectTo, @NotNull Optional<String[]> credentials);

  private @NotNull Optional<String[]> credentials() {
    return (username.isEmpty() || password.isEmpty()) ? Optional.empty() : Optional.of(new String[]{username, password});
  }
}