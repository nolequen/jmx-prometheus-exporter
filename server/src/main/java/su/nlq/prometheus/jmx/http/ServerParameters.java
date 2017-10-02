package su.nlq.prometheus.jmx.http;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.InetSocketAddress;

public abstract class ServerParameters {

  public final @NotNull File config() {
    return new File(getConfig());
  }

  public final @NotNull InetSocketAddress address() {
    final String host = getHost();
    return host.isEmpty() ? new InetSocketAddress(getPort()) : new InetSocketAddress(host, getPort());
  }

  public final @NotNull ExpositionFormat format() {
    return getFormat();
  }

  protected abstract @NotNull String getConfig();

  protected abstract int getPort();

  protected abstract @NotNull String getHost();

  protected abstract @NotNull ExpositionFormat getFormat();
}
