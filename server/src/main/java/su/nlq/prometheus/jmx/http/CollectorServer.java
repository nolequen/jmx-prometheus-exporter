package su.nlq.prometheus.jmx.http;

import io.prometheus.client.exporter.MetricsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nlq.prometheus.jmx.Configuration;
import su.nlq.prometheus.jmx.JmxCollector;
import su.nlq.prometheus.jmx.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;

public final class CollectorServer {
  private static final @NotNull String METRICS_PATH = "/metrics";

  private final @NotNull Configuration configuration;
  private @Nullable Server server;

  public static @NotNull Optional<CollectorServer> create(@NotNull File config) {
    try {
      return Optional.of(new CollectorServer(config));
    } catch (IOException e) {
      Logger.instance.error("Failed to create collector server", e);
      return Optional.empty();
    }
  }

  private CollectorServer(@NotNull File config) throws IOException {
    configuration = Configuration.parse(config);
  }

  public @NotNull CollectorServer init(@NotNull InetSocketAddress address) {
    try {
      Log.setLog(new Slf4jLog());

      final Server serverInstance = new Server(address);

      final GzipHandler gzip = new GzipHandler();
      final ServletContextHandler handler = new ServletContextHandler(gzip, "/");
      gzip.setHandler(handler);
      serverInstance.setHandler(gzip);
      handler.addServlet(new ServletHolder(new MetricsServlet()), METRICS_PATH);

      serverInstance.start();

      server = serverInstance;

      Logger.instance.info("Prometheus server with JMX metrics started at " + address);
    } catch (Exception e) {
      Logger.instance.error("Failed to start server at " + address, e);
    }
    return this;
  }

  public void start() {
    JmxCollector.register(configuration.connections(), configuration.whitelist(), configuration.blacklist());
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
