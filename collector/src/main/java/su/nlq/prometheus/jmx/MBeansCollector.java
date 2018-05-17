package su.nlq.prometheus.jmx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nlq.prometheus.jmx.logging.Logger;

import javax.management.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MBeansCollector {
  private final @NotNull QueryExp query;

  public static @NotNull MBeansCollector create(@NotNull List<String> whitelist, @NotNull List<String> blacklist) {
    return whitelist.isEmpty()
        ? new ExtrusiveCollector(blacklist)
        : new InclusiveCollector(whitelist, blacklist);
  }

  protected MBeansCollector(@NotNull List<String> blacklist) {
    this.query = new ExcludeQuery(blacklist);
  }

  public abstract @NotNull Collection<ObjectInstance> collect(@NotNull MBeanServerConnection connection);

  protected final @NotNull Collection<ObjectInstance> query(@NotNull MBeanServerConnection connection, @Nullable ObjectName name) {
    try {
      return connection.queryMBeans(name, query);
    } catch (IOException e) {
      Logger.instance.debug("Failed to query beans of '" + name + '\'', e);
      return Collections.emptySet();
    }
  }

  private static final class InclusiveCollector extends MBeansCollector {
    private final @NotNull Collection<ObjectName> names;

    public InclusiveCollector(@NotNull List<String> whitelist, @NotNull List<String> blacklist) {
      super(blacklist);
      this.names = new ArrayList<>(whitelist.size());
      whitelist.forEach(name -> {
        try {
          this.names.add(new ObjectName(name));
        } catch (MalformedObjectNameException e) {
          Logger.instance.warn("Failed to construct " + name, e);
        }
      });
    }

    @Override
    public @NotNull Collection<ObjectInstance> collect(@NotNull MBeanServerConnection connection) {
      return names.stream().flatMap(name -> query(connection, name).stream()).collect(Collectors.toSet());
    }
  }

  private static final class ExtrusiveCollector extends MBeansCollector {

    public ExtrusiveCollector(@NotNull List<String> blacklist) {
      super(blacklist);
    }

    @Override
    public @NotNull Collection<ObjectInstance> collect(@NotNull MBeanServerConnection connection) {
      return query(connection, null);
    }
  }
}
