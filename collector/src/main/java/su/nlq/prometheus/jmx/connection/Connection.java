package su.nlq.prometheus.jmx.connection;

import javax.management.MBeanServerConnection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Connection extends Consumer<BiConsumer<String, MBeanServerConnection>> {
}
