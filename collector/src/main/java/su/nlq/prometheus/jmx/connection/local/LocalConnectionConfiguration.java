package su.nlq.prometheus.jmx.connection.local;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.Connection;

import javax.xml.bind.annotation.XmlType;
import java.util.function.Supplier;

@XmlType(name = "local-connection")
public final class LocalConnectionConfiguration implements Supplier<Connection> {

  @Override
  public @NotNull Connection get() {
    return new LocalConnection();
  }
}
