package su.nlq.prometheus.jmx.connection.local;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.Connection;
import su.nlq.prometheus.jmx.connection.ConnectionConfiguration;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "local-connection")
public final class LocalConnectionConfiguration extends ConnectionConfiguration {

  @Override
  public @NotNull Connection get() {
    return new LocalConnection(name());
  }
}
