package su.nlq.prometheus.jmx.connection.remote;

import javax.management.remote.JMXConnector;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Connector extends Consumer<BiConsumer<String, JMXConnector>> {
}
