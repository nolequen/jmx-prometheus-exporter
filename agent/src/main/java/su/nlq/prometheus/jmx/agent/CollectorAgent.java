package su.nlq.prometheus.jmx.agent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.args4j.Argument;
import su.nlq.prometheus.jmx.arguments.Arguments;
import su.nlq.prometheus.jmx.http.ExpositionFormat;
import su.nlq.prometheus.jmx.http.Launcher;
import su.nlq.prometheus.jmx.http.ServerParameters;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public enum CollectorAgent {
  ;

  private static @NotNull AtomicBoolean launched = new AtomicBoolean();

  public static void premain(@Nullable String arguments) {
    if (launched.compareAndSet(false, true)) {
      final String[] splitted = Optional.ofNullable(arguments).orElse("").split(":");
      Launcher.launch(Arguments.of(splitted, new AgentArguments()));
    }
  }

  private static final class AgentArguments extends ServerParameters {

    @Argument(index = 0, metaVar = "CONFIG", usage = "configuration xml file (required)", required = true)
    private @NotNull String config = "";

    @Argument(index = 1, metaVar = "PORT", usage = "port (required)", required = true)
    private int port = 0;

    @Argument(index = 2, metaVar = "HOST", usage = "host", required = false)
    private @NotNull String host = "localhost";

    @Argument(index = 3, metaVar = "FORMAT", usage = "exposition format", required = false)
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
