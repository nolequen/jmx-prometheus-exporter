package su.nlq.prometheus.jmx.http;

import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.exporter.ProtobufMetricsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jetbrains.annotations.NotNull;

public enum ExpositionFormat {

  Text {
    @Override
    void handler(@NotNull Server server) {
      final GzipHandler gzip = new GzipHandler();
      final ServletContextHandler handler = new ServletContextHandler(gzip, "/");
      gzip.setHandler(handler);
      server.setHandler(gzip);
      handler.addServlet(new ServletHolder(new MetricsServlet()), METRICS_PATH);
    }
  },

  Protobuf {
    @Override
    void handler(@NotNull Server server) {
      final ServletContextHandler handler = new ServletContextHandler();
      handler.setContextPath("/");
      server.setHandler(handler);
      handler.addServlet(new ServletHolder(new ProtobufMetricsServlet()), METRICS_PATH);
    }
  };

  private static final @NotNull String METRICS_PATH = "/metrics";

  abstract void handler(@NotNull Server server);
}
