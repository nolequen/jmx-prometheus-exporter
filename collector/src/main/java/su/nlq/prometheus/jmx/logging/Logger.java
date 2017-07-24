package su.nlq.prometheus.jmx.logging;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

public enum Logger {
  ;
  public static final @NotNull org.slf4j.Logger instance = LoggerFactory.getLogger("jmx-prometheus-export");
}
