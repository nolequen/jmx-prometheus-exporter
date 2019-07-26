package su.nlq.prometheus.jmx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.management.*;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ExcludeQuery implements QueryExp {
  private final @NotNull Collection<Pattern> patterns;

  public ExcludeQuery(@NotNull Collection<String> patterns) {
    this.patterns = patterns.stream().map(Pattern::compile).collect(Collectors.toList());
  }

  @SuppressWarnings("MethodWithTooExceptionsDeclared")
  @Override
  public boolean apply(@Nullable ObjectName name) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
    return name != null && patterns.stream().noneMatch(pattern -> pattern.matcher(name.getCanonicalName()).matches());
  }

  @Override
  public void setMBeanServer(@Nullable MBeanServer mBeanServer) {
    //do nothing
  }
}
