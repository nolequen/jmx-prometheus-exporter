package su.nlq.prometheus.jmx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nlq.prometheus.jmx.logging.Logger;

import javax.management.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class MBeansCollector {
  private final @NotNull Collection<ObjectName> names;
  private final @NotNull QueryExp query;

  public MBeansCollector(@NotNull List<String> names, @NotNull List<String> exclude) {
    this.names = getObjectNames(names);
    this.query = new BlacklistQuery(getObjectNames(exclude));
  }

  public @NotNull Collection<ObjectInstance> collect(@NotNull MBeanServerConnection connection) {
    return names.isEmpty()
        ? new HashSet<>(query(connection, null))
        : names.stream().flatMap(name -> query(connection, name).stream()).collect(Collectors.toSet());
  }

  private static @NotNull Collection<ObjectName> getObjectNames(@NotNull Iterable<String> names) {
    final Collection<ObjectName> result = new ArrayList<>();
    names.forEach(name -> {
      try {
        result.add(new ObjectName(name));
      } catch (MalformedObjectNameException e) {
        Logger.instance.debug("Failed to construct " + name, e);
      }
    });
    return result;
  }

  private @NotNull Collection<ObjectInstance> query(@NotNull MBeanServerConnection connection, @Nullable ObjectName name) {
    try {
      return connection.queryMBeans(name, query);
    } catch (IOException e) {
      Logger.instance.debug("Failed to query beans of '" + name + '\'', e);
      return Collections.emptySet();
    }
  }

  private static final class BlacklistQuery extends QueryEval implements QueryExp {
    private final @NotNull Collection<ObjectName> blacklist;

    public BlacklistQuery(@NotNull Collection<ObjectName> blacklist) {
      this.blacklist = blacklist;
    }

    @Override
    public boolean apply(@NotNull ObjectName name) {
      return !blacklist.contains(name);
    }
  }
}
