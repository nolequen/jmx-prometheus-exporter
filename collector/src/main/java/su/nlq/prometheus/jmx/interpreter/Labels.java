package su.nlq.prometheus.jmx.interpreter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.IntStream;

public final class Labels {
  private final @NotNull List<String> names = new ArrayList<>();
  private final @NotNull List<String> values = new ArrayList<>();

  public void add(@NotNull String name, @NotNull String value) {
    if (!name.isEmpty() && !value.isEmpty()) {
      names.add(name);
      values.add(value);
    }
  }

  public @NotNull List<String> getNames() {
    return names;
  }

  public @NotNull List<String> getValues() {
    return values;
  }

  @Override
  public @NotNull String toString() {
    final StringJoiner joiner = new StringJoiner(", ");
    IntStream.range(0, Math.min(names.size(), values.size()))
        .mapToObj(index -> names.get(index) + ':' + values.get(index))
        .forEach(joiner::add);
    return joiner.toString();
  }
}
