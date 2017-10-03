package su.nlq.prometheus.jmx.correction;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.regex.Pattern;

public enum Correction {
  Key(Replacement.MultipleUnderscores.compose(Replacement.UnsafeChars)),
  Value(Replacement.Quotes);

  private final @NotNull Operator correction;

  private Correction(@NotNull Operator correction) {
    this.correction = correction;
  }

  public @NotNull String apply(@NotNull String value) {
    return correction.apply(value);
  }

  private enum Replacement implements Operator {
    UnsafeChars(Pattern.compile("[^a-zA-Z0-9_]"), "_"),
    MultipleUnderscores(Pattern.compile("__+"), "_"),
    Quotes(Pattern.compile("^\"|\"$"), "");

    private final @NotNull Pattern pattern;
    private final @NotNull String replacement;

    private Replacement(@NotNull Pattern pattern, @NotNull String replacement) {
      this.pattern = pattern;
      this.replacement = replacement;
    }

    @Override
    public @NotNull String apply(@NotNull String value) {
      return pattern.matcher(value).replaceAll(replacement);
    }

    public @NotNull Operator compose(@NotNull Replacement before) {
      return (String v) -> apply(before.apply(v));
    }
  }

  private interface Operator extends Serializable {

    @NotNull String apply(@NotNull String value);
  }
}
