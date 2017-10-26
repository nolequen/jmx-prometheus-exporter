package su.nlq.prometheus.jmx.connection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class ConnectionConfiguration implements Supplier<Connection> {
  @XmlAttribute
  private @Nullable String name;

  public final @NotNull Optional<String> name() {
    return Optional.ofNullable(name);
  }
}
