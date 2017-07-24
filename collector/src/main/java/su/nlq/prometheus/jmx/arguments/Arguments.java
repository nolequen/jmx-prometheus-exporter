package su.nlq.prometheus.jmx.arguments;

import org.jetbrains.annotations.NotNull;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import su.nlq.prometheus.jmx.logging.Logger;

import java.io.StringWriter;
import java.util.Optional;

public enum Arguments {
  ;

  @SuppressWarnings("StaticMethodNamingConvention")
  public static @NotNull <T> Optional<T> of(@NotNull String[] args, @NotNull T bean) {
    final CmdLineParser parser = new CmdLineParser(bean);
    try {
      parser.parseArgument(args);
      return Optional.of(bean);
    } catch (CmdLineException e) {
      Logger.instance.error(e.getMessage());
      final StringWriter writer = new StringWriter();
      parser.printUsage(writer, null);
      Logger.instance.info("Usage:\n" + writer.toString());
      final String example = parser.printExample(OptionHandlerFilter.ALL);
      if (!example.isEmpty()) {
        Logger.instance.info("Example: java -jar <app.jar> " + example);
      }
    }
    return Optional.empty();
  }
}
