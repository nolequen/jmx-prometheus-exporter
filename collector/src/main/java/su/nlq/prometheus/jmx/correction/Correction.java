package su.nlq.prometheus.jmx.correction;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public enum Correction implements UnaryOperator<String> {
  UnsafeChars(Pattern.compile("[^a-zA-Z0-9_]")),
  MultipleUnderscores(Pattern.compile("__+"));

  private static final @NotNull Function<String, String> correction = Correction.MultipleUnderscores.compose(Correction.UnsafeChars);

  private final @NotNull Pattern pattern;

  public static @NotNull String correct(@NotNull String value) {
    return correction.apply(value);
  }

  private Correction(@NotNull Pattern pattern) {
    this.pattern = pattern;
  }

  @Override
  public @NotNull String apply(@NotNull String value) {
    return pattern.matcher(value).replaceAll("_");
  }
}
