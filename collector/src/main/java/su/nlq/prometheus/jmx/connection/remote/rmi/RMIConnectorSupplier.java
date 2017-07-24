package su.nlq.prometheus.jmx.connection.remote.rmi;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.remote.ConnectorSupplier;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.rmi.RMIConnectorServer;
import javax.naming.Context;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public final class RMIConnectorSupplier extends ConnectorSupplier {
  private final boolean ssl;

  public RMIConnectorSupplier(@NotNull String address, @NotNull Optional<String[]> credentials, boolean ssl) {
    super("service:jmx:rmi:///jndi/rmi://" + address + "/jmxrmi", credentials);
    this.ssl = ssl;
  }

  @Override
  public @NotNull JMXConnector get() throws IOException {
    final Map<String, Object> environment = getEnvironment();
    if (ssl) {
      environment.put(Context.SECURITY_PROTOCOL, "ssl");
      final SslRMIClientSocketFactory clientSocketFactory = new SslRMIClientSocketFactory();
      environment.put(RMIConnectorServer.RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE, clientSocketFactory);
      environment.put("com.sun.jndi.rmi.factory.socket", clientSocketFactory);
    }
    return JMXConnectorFactory.connect(getURL(), environment);
  }
}
