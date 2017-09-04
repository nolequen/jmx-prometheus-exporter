package su.nlq.prometheus.jmx.bean;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.correction.Correction;

import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class MBean {
  private static final @NotNull String TYPE_PROPERTY_NAME = "type";
  private static final @NotNull String ATTRIBUTE_PROPERTY_NAME = "attribute";

  private final @NotNull String attribute;
  private final @NotNull String name;
  private final @NotNull Map<String, String> labels;

  @SuppressWarnings("StaticMethodNamingConvention")
  public static @NotNull Optional<MBean> of(@NotNull ObjectName name, @NotNull MBeanAttributeInfo info) {
    final Map<String, String> properties = name.getKeyPropertyList();
    final String type = properties.get(TYPE_PROPERTY_NAME);
    if (type == null) {
      return Optional.empty();
    }

    final Map<String, String> labels = properties.entrySet().stream()
        .filter(entry -> !entry.getKey().equals(TYPE_PROPERTY_NAME))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    return Optional.of(new MBean(name.getDomain() + ':' + type, info.getName(), labels));
  }

  private MBean(@NotNull String name, @NotNull String attribute, @NotNull Map<String, String> labels) {
    this.name = name;
    this.attribute = attribute;
    this.labels = labels;
  }

  public @NotNull MBean compose(@NotNull String key) {
    return new MBean(name + '_' + attribute, key, new HashMap<>(labels));
  }

  public @NotNull MBean labeled(@NotNull String key, @NotNull String value) {
    final MBean bean = new MBean(name, attribute, new HashMap<>(labels));
    bean.labels.put(key, value);
    return bean;
  }

  @Override
  public @NotNull String toString() {
    return name + " [" + getLabels() + ']';
  }

  public @NotNull String getName() {
    return Correction.correct(name);
  }

  public @NotNull String getHelp() {
    return name;
  }

  public @NotNull Labels getLabels() {
    final Labels result = new Labels();
    result.add(ATTRIBUTE_PROPERTY_NAME, attribute);
    labels.forEach((k, v) -> result.add(Correction.correct(k), v));
    return result;
  }
}
