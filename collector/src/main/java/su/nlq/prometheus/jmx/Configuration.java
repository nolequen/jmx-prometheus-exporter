package su.nlq.prometheus.jmx;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.connection.Connection;
import su.nlq.prometheus.jmx.connection.local.LocalConnection;
import su.nlq.prometheus.jmx.connection.local.LocalConnectionConfiguration;
import su.nlq.prometheus.jmx.connection.remote.jmxmp.JMXMPConnectionConfiguration;
import su.nlq.prometheus.jmx.connection.remote.rmi.RMIConnectionConfiguration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;

@XmlAccessorOrder
@XmlRootElement(name = "configuration")
public final class Configuration {

  @XmlElementWrapper(name = "connections")
  @XmlElements({
      @XmlElement(name = "local", type = LocalConnectionConfiguration.class),
      @XmlElement(name = "jmxmp", type = JMXMPConnectionConfiguration.class),
      @XmlElement(name = "rmi", type = RMIConnectionConfiguration.class)
  })
  private @NotNull List<Supplier<Connection>> connections = new ArrayList<>(Arrays.asList(LocalConnection::new));

  @XmlElementWrapper(name = "whitelist")
  @XmlElement(name = "name")
  private @NotNull List<String> whitelist = new ArrayList<>();

  @XmlElementWrapper(name = "blacklist")
  @XmlElement(name = "name")
  private @NotNull List<String> blacklist = new ArrayList<>();

  public static @NotNull Configuration parse(@NotNull File file) throws IOException {
    try (final InputStream input = new FileInputStream(file)) {
      return (Configuration) unmarshaller(Configuration.class).unmarshal(input);
    } catch (JAXBException e) {
      throw new IOException(e);
    }
  }

  public @NotNull Iterable<Connection> connections() {
    return connections.stream().map(Supplier::get).collect(Collectors.toList());
  }

  public @NotNull List<String> whitelist() {
    return unmodifiableList(whitelist);
  }

  public @NotNull List<String> blacklist() {
    return unmodifiableList(blacklist);
  }

  private static @NotNull <T> Unmarshaller unmarshaller(@NotNull Class<T> aClass) throws JAXBException {
    final JAXBContext context = JAXBContext.newInstance(aClass);
    final Unmarshaller unmarshaller = context.createUnmarshaller();
    SchemaGenerator.load(context).ifPresent(unmarshaller::setSchema);
    return unmarshaller;
  }
}