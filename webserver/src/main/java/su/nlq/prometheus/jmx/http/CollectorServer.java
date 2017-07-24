package su.nlq.prometheus.jmx.http;

import io.prometheus.client.exporter.MetricsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.args4j.Option;
import su.nlq.prometheus.jmx.JmxCollector;
import su.nlq.prometheus.jmx.arguments.Arguments;
import su.nlq.prometheus.jmx.logging.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

public enum CollectorServer {
  ;

  public static void main(@NotNull String[] args) {
    Arguments.of(args, new CommandLineParams()).ifPresent(CollectorServer::start);
  }

  public static void start(@NotNull ServerParameters params) {
    try {
      JmxCollector.register(params.config());
    } catch (IOException e) {
      Logger.instance.error("Failed to register collector", e);
      return;
    }
    start(params.address());
  }

  private static void start(@NotNull InetSocketAddress address) {
    try {
      Log.setLog(new Slf4jLog());

      final ServletHandler handler = new ServletHandler();
      handler.addServletWithMapping(new ServletHolder(new MetricsServlet()), "/metrics");

      final Server server = new Server(address);
      server.setHandler(handler);
      server.start();
      Logger.instance.info("Prometheus server with JMX metrics started at " + address);
    } catch (Exception e) {
      Logger.instance.error("Failed to start server at " + address, e);
    }
  }

  private static final class CommandLineParams extends ServerParameters {

    @Option(name = "-c", aliases = {"--configuration"}, usage = "configuration xml file", required = true)
    private @NotNull String config = "";

    @Option(name = "-h", aliases = {"--host"}, usage = "web server host (any local address by default)", required = false)
    private @NotNull String host = "";

    @Option(name = "-p", aliases = {"--port"}, usage = "web server port", required = true)
    private int port = 0;

    @Override
    protected @NotNull String getConfig() {
      return config;
    }

    @Override
    protected int getPort() {
      return port;
    }

    @Override
    protected @NotNull String getHost() {
      return host;
    }
  }
}
