package su.nlq.prometheus.jmx.scraper;

import org.jetbrains.annotations.NotNull;
import su.nlq.prometheus.jmx.interpreter.MBean;

public interface Receiver {

  interface Consumer {

    @SuppressWarnings("InstanceMethodNamingConvention")
    void to(@NotNull Receiver receiver);
  }

  void accept(@NotNull MBean bean, @NotNull Object value);
}
