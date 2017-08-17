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

  private final @NotNull Optional<MBean> parent;
  private final @NotNull String type;
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
    labels.put("attribute", info.getName());

    return Optional.of(new MBean(Optional.empty(), name.getDomain() + ':' + type, labels));
  }

  private MBean(@NotNull Optional<MBean> parent, @NotNull String type, @NotNull Map<String, String> labels) {
    this.parent = parent;
    this.type = type;
    this.labels = labels;
  }

  public @NotNull MBean compose(@NotNull String name) {
    return new MBean(Optional.of(this), name, new HashMap<>(labels));
  }

  public @NotNull MBean labeled(@NotNull String key, @NotNull String value) {
    final MBean bean = new MBean(parent, type, new HashMap<>(labels));
    bean.labels.put(key, value);
    return bean;
  }

  @Override
  public @NotNull String toString() {
    return getFullName() + " [" + getLabels() + ']';
  }

  public @NotNull String getName() {
    return getParentName().orElse(type);
  }

  public @NotNull String getHelp() {
    return getName();
  }

  public @NotNull Labels getLabels() {
    final Labels result = new Labels();
    parent.ifPresent(bean -> result.add(TYPE_PROPERTY_NAME, type));
    labels.forEach((k, v) -> result.add(Correction.correct(k), v));
    return result;
  }

  private @NotNull String getFullName() {
    return getParentName().map(name -> name + '.' + type).orElse(type);
  }

  private @NotNull Optional<String> getParentName() {
    return parent.map(MBean::getFullName);
  }
}
