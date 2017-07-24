package su.nlq.prometheus.jmx.agent;

import io.prometheus.client.hotspot.DefaultExports;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.args4j.Argument;
import su.nlq.prometheus.jmx.arguments.Arguments;
import su.nlq.prometheus.jmx.http.CollectorServer;
import su.nlq.prometheus.jmx.http.ServerParameters;

import java.lang.instrument.Instrumentation;

public enum CollectorAgent {
  ;

  public static void premain(@NotNull String argument, @NotNull Instrumentation instrumentation) {
    Arguments.of(argument.split(":"), new AgentArguments()).ifPresent(arguments -> {
      CollectorServer.start(arguments);
      DefaultExports.initialize();
    });
  }

  private static final class AgentArguments extends ServerParameters {

    @Argument(index = 0, metaVar = "HOST", usage = "host", required = false)
    private @NotNull String host = "";

    @Argument(index = 1, metaVar = "PORT", usage = "port", required = true)
    private int port = 0;

    @Argument(index = 2, metaVar = "FILE", usage = "configuration xml file", required = true)
    private @NotNull String config = "";

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
