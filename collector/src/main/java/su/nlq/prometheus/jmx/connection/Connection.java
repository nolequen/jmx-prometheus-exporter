package su.nlq.prometheus.jmx.connection;

import javax.management.MBeanServerConnection;
import java.util.function.Consumer;

public interface Connection extends Consumer<Consumer<MBeanServerConnection>> {
}
