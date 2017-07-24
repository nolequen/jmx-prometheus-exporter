package su.nlq.prometheus.jmx.connection;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.remote.CachedConnector;
import su.nlq.prometheus.jmx.connection.remote.ClosableConnector;
import su.nlq.prometheus.jmx.connection.remote.ConnectorSupplier;
import su.nlq.prometheus.jmx.connection.remote.RemoteConnection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Optional;
import java.util.function.Supplier;

@XmlType(name = "remote-connection")
public abstract class ConnectionConfiguration implements Supplier<Connection> {
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
    final ConnectorSupplier connector = connector(address, credentials());
    return new RemoteConnection(keep ? new CachedConnector(connector) : new ClosableConnector(connector));
  }

  protected abstract @NotNull ConnectorSupplier connector(@NotNull String connectTo, @NotNull Optional<String[]> credentials);

  private @NotNull Optional<String[]> credentials() {
    return (username.isEmpty() || password.isEmpty()) ? Optional.empty() : Optional.of(new String[]{username, password});
  }
}