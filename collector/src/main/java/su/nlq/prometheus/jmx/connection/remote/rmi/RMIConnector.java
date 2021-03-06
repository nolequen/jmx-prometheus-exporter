package su.nlq.prometheus.jmx.connection.remote.rmi;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.remote.Connector;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnectorServer;
import javax.naming.Context;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public final class RMIConnector extends Connector {
  private final boolean ssl;

  public RMIConnector(@NotNull String address, @NotNull Optional<String[]> credentials, boolean ssl) {
    super(address, credentials);
    this.ssl = ssl;
  }

  @Override
  public @NotNull JMXConnector connect() throws IOException {
    final Map<String, Object> environment = environment();
    if (ssl) {
      environment.put(Context.SECURITY_PROTOCOL, "ssl");
      final SslRMIClientSocketFactory clientSocketFactory = new SslRMIClientSocketFactory();
      environment.put(RMIConnectorServer.RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE, clientSocketFactory);
      environment.put("com.sun.jndi.rmi.factory.socket", clientSocketFactory);
    }
    final JMXServiceURL address = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + address() + "/jmxrmi");
    return JMXConnectorFactory.connect(address, environment);
  }
}
