package su.nlq.prometheus.jmx.http;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.args4j.Option;
import su.nlq.prometheus.jmx.arguments.Arguments;

import java.util.Optional;

public final class Launcher implements Daemon {
  private @NotNull Optional<ServerParameters> parameters = Optional.empty();
  private @NotNull Optional<CollectorServer> server = Optional.empty();

  public static void main(@NotNull String[] arguments) {
    launch(CommandLineParams.parse(arguments));
  }

  public static @NotNull Optional<CollectorServer> launch(@NotNull Optional<ServerParameters> parameters) {
    return parameters
        .flatMap(params -> CollectorServer.create(params.config())
            .map(server -> {
              server.start(params.address(), params.format());
              return server;
            }));
  }

  @Override
  public void init(@NotNull DaemonContext context) {
    parameters = CommandLineParams.parse(context.getArguments());
  }

  @Override
  public void start() {
    server = launch(parameters);
  }

  @Override
  public void stop() {
    server.ifPresent(CollectorServer::stop);
  }

  @Override
  public void destroy() {
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

    public static @NotNull Optional<ServerParameters> parse(@NotNull String[] arguments) {
      return Arguments.of(arguments, new CommandLineParams());
    }

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
