package su.nlq.prometheus.jmx.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.args4j.Option;
import su.nlq.prometheus.jmx.Configuration;
import su.nlq.prometheus.jmx.JmxCollector;
import su.nlq.prometheus.jmx.logging.Logger;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.InetSocketAddress;

public enum CollectorServer {
  ;

  public static void start(@NotNull ServerParameters params) {
    try {
      final Configuration configuration = Configuration.parse(params.config());
      JmxCollector.register(configuration.connections(), configuration.whitelist(), configuration.blacklist());
    } catch (IOException | JAXBException e) {
      Logger.instance.error("Failed to register collector", e);
      return;
    }
    start(params.address(), params.format());
  }

  private static void start(@NotNull InetSocketAddress address, @NotNull ExpositionFormat format) {
    try {
      Log.setLog(new Slf4jLog());

      final Server server = new Server(address);
      format.handler(server);
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

    @Option(name = "-f", aliases = {"--format"}, usage = "exposition format", required = false)
    private @NotNull ExpositionFormat format = ExpositionFormat.Text;

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

    @Override
    protected @NotNull ExpositionFormat getFormat() {
      return format;
    }
  }
}
