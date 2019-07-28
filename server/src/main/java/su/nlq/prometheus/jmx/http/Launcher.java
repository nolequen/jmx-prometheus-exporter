package su.nlq.prometheus.jmx.http;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.args4j.Option;
import su.nlq.prometheus.jmx.arguments.Arguments;

import java.util.Optional;

public final class Launcher implements Daemon {
  private @NotNull Optional<CollectorServer> server = Optional.empty();

  public static void main(@NotNull String[] arguments) {
    create(CommandLineParams.parse(arguments)).ifPresent(CollectorServer::start);
  }

  public static @NotNull Optional<CollectorServer> create(@NotNull Optional<ServerParameters> parameters) {
    return parameters
        .flatMap(params -> CollectorServer.create(params.config())
            .map(server -> server.init(params.address())));
  }

  @Override
  public void init(@NotNull DaemonContext context) {
    server = create(CommandLineParams.parse(context.getArguments()));
  }

  @Override
  public void start() {
    server.ifPresent(CollectorServer::start);
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
  }
}
