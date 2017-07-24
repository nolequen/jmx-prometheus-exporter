package su.nlq.prometheus.jmx.interpreter;

import io.prometheus.client.Collector;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@XmlType(name = "rules")
public final class RuleConfiguration {

  @XmlAttribute(required = true)
  private @NotNull String pattern = "";

  @XmlElement
  private @NotNull String name = "";

  @XmlElement
  private @NotNull String help = "";

  @XmlElement
  @XmlJavaTypeAdapter(TypeAdapter.class)
  private @NotNull Collector.Type type = Collector.Type.GAUGE;

  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  @XmlJavaTypeAdapter(LabelsAdapter.class)
  private @NotNull Map<String, String> labels = new HashMap<>();

  public @NotNull String getName(@NotNull Matcher matcher, @NotNull Supplier<String> defaultName) {
    return Correction.correct(get(matcher, name, defaultName));
  }

  public @NotNull String getHelp(@NotNull Matcher matcher, @NotNull Supplier<String> defaultHelp) {
    return get(matcher, help, defaultHelp);
  }

  public @NotNull Labels getLabels(@NotNull Matcher matcher, @NotNull Supplier<Labels> defaultLabels) {
    if (labels.isEmpty()) {
      return defaultLabels.get();
    }
    final Labels result = new Labels();
    labels.forEach((key, value) -> result.add(Correction.correct(matcher.replaceAll(key)), matcher.replaceAll(value)));
    return result;
  }

  public @NotNull Collector.Type getType() {
    return type;
  }

  public @NotNull Pattern pattern() {
    return Pattern.compile(pattern);
  }

  private static @NotNull String get(@NotNull Matcher matcher, @NotNull String value, @NotNull Supplier<String> defaultValue) {
    return value.isEmpty() ? defaultValue.get() : matcher.replaceAll(value);
  }

  private static final class LabelsAdapter extends XmlAdapter<LabelsConfiguration, Map<String, String>> {

    @Override
    public @NotNull LabelsConfiguration marshal(@NotNull Map<String, String> map) {
      return new LabelsConfiguration(map.entrySet().stream().map(entry -> new Label(entry.getKey(), entry.getValue())).collect(Collectors.toList()));
    }

    @Override
    public @NotNull Map<String, String> unmarshal(@NotNull LabelsConfiguration labels) {
      return labels.labels.stream().collect(Collectors.toMap(label -> label.name, label -> label.value));
    }
  }

  private static final class TypeAdapter extends XmlAdapter<String, Collector.Type> {

    @Override
    public @NotNull Collector.Type unmarshal(@NotNull String name) {
      return Collector.Type.valueOf(name);
    }

    @Override
    public @NotNull String marshal(@NotNull Collector.Type type) {
      return type.name();
    }
  }

  @XmlType(name = "labels")
  private static final class LabelsConfiguration {
    @XmlElement(name = "label")
    private @NotNull List<Label> labels;

    public LabelsConfiguration(@NotNull List<Label> labels) {
      this.labels = labels;
    }

    @SuppressWarnings("unused") // JAXB needs it
    private LabelsConfiguration() {
      this(new ArrayList<>());
    }
  }

  private static final class Label {
    @XmlAttribute(required = true)
    private @NotNull String name = "";
    @XmlAttribute(required = true)
    private @NotNull String value = "";

    public Label(@NotNull String name, @NotNull String value) {
      this.name = name;
      this.value = value;
    }

    @SuppressWarnings("unused") // JAXB needs it
    private Label() {
    }
  }
}
