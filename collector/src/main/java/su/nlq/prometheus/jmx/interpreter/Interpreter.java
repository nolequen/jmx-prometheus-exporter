package su.nlq.prometheus.jmx.interpreter;

import io.prometheus.client.Collector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Interpreter {

  public interface SampleConsumer {

    void accept(@NotNull Collector.MetricFamilySamples.Sample sample, @NotNull Collector.Type type, @NotNull String help);
  }

  private final @NotNull Collection<Rule> rules;

  public Interpreter(@NotNull Collection<RuleConfiguration> rules) {
    this.rules = rules.stream().map(Rule::new).collect(Collectors.toList());
  }

  public @NotNull Consumer<SampleConsumer> accept(@NotNull MBean bean, @NotNull Object value) {
    return consumer -> rules.forEach(rule -> rule.apply(bean, value).accept(consumer));
  }

  private static final class Rule {
    private final @NotNull RuleConfiguration configuration;
    private final @NotNull Pattern pattern;

    public Rule(@NotNull RuleConfiguration configuration) {
      this.pattern = configuration.pattern();
      this.configuration = configuration;
    }

    public @NotNull Consumer<SampleConsumer> apply(@NotNull MBean bean, @NotNull Object value) {
      return consumer -> {
        final Matcher matcher = pattern.matcher(bean.getName() + ": " + value);
        if (matcher.matches()) {
          asNumber(value, v -> apply(bean, matcher, consumer, v.doubleValue()));
        }
      };
    }

    private static void asNumber(@NotNull Object object, @NotNull Consumer<Number> consumer) {
      if (object instanceof Number) {
        consumer.accept((Number) object);
      }
    }

    private void apply(@NotNull MBean bean, @NotNull Matcher matcher, @NotNull SampleConsumer consumer, double value) {
      final String name = configuration.getName(matcher, bean::getName);
      if (name.isEmpty()) {
        return;
      }
      final String help = configuration.getHelp(matcher, bean::getHelp);
      if (help.isEmpty()) {
        return;
      }
      final Labels labels = configuration.getLabels(matcher, bean::getLabels);
      consumer.accept(
          new Collector.MetricFamilySamples.Sample(name, labels.getNames(), labels.getValues(), value),
          configuration.getType(),
          help);
    }
  }
}
