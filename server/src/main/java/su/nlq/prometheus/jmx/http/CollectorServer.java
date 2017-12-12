package su.nlq.prometheus.jmx.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nlq.prometheus.jmx.Configuration;
import su.nlq.prometheus.jmx.JmxCollector;
import su.nlq.prometheus.jmx.logging.Logger;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;

public final class CollectorServer {
  private final @NotNull Configuration configuration;
  private @Nullable Server server;

  public static @NotNull Optional<CollectorServer> create(@NotNull File config) {
    try {
      return Optional.of(new CollectorServer(config));
    } catch (IOException | JAXBException e) {
      Logger.instance.error("Failed to create collector server", e);
      return Optional.empty();
    }
  }

  private CollectorServer(@NotNull File config) throws IOException, JAXBException {
    configuration = Configuration.parse(config);
  }

  public void start(@NotNull InetSocketAddress address, @NotNull ExpositionFormat format) {
    JmxCollector.register(configuration.connections(), configuration.whitelist(), configuration.blacklist());
    try {
      Log.setLog(new Slf4jLog());

      final Server serverInstance = new Server(address);
      format.handler(serverInstance);
      serverInstance.start();

      this.server = serverInstance;

      Logger.instance.info("Prometheus server with JMX metrics started at " + address);
    } catch (Exception e) {
      Logger.instance.error("Failed to start server at " + address, e);
    }
  }

  public void stop() {
    if (server != null) {
      try {
        server.stop();
      } catch (Exception e) {
        Logger.instance.error("Failed to stop server", e);
      }
    }
  }
}
