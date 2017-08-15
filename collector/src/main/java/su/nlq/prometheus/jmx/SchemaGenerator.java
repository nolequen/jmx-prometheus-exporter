package su.nlq.prometheus.jmx;

import org.jetbrains.annotations.NotNull;
import org.kohsuke.args4j.Argument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import su.nlq.prometheus.jmx.arguments.Arguments;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SchemaGenerator {
  private static final @NotNull Logger logger = LoggerFactory.getLogger(SchemaGenerator.class);

  private final @NotNull String filename;

  public static void main(@NotNull String[] args) {
    Arguments.of(args, new GeneratorArguments())
        .map(SchemaGenerator::new)
        .ifPresent(SchemaGenerator::generate);
  }

  public static @NotNull Optional<Schema> load(@NotNull JAXBContext context) {
    try {
      final List<ByteArrayOutputStream> outputs = new ArrayList<>();
      context.generateSchema(new SchemaOutputResolver() {
        @Override
        public @NotNull Result createOutput(@NotNull String namespace, @NotNull String suggestedFileName) {
          final ByteArrayOutputStream output = new ByteArrayOutputStream();
          outputs.add(output);
          final StreamResult result = new StreamResult(output);
          result.setSystemId("");
          return result;
        }
      });
      return Optional.ofNullable(
          SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
              .newSchema(outputs.stream()
                  .map(ByteArrayOutputStream::toByteArray)
                  .map(ByteArrayInputStream::new)
                  .map(input -> new StreamSource(input, ""))
                  .toArray(StreamSource[]::new))
      );
    } catch (IOException | SAXException e) {
      logger.error("Failed to load schema", e);
      return Optional.empty();
    }
  }

  private SchemaGenerator(@NotNull GeneratorArguments arguments) {
    this.filename = arguments.filename;
  }

  private void generate() {
    try {
      JAXBContext.newInstance(Configuration.class).generateSchema(new SchemaOutputResolver() {
        @Override
        public @NotNull Result createOutput(@NotNull String namespace, @NotNull String suggestedFileName) throws MalformedURLException {
          final File file = new File(filename);
          final StreamResult result = new StreamResult(file);
          result.setSystemId(file.toURI().toURL().toString());
          return result;
        }
      });
      logger.info("Schema successfully generated to " + filename);
    } catch (JAXBException | IOException e) {
      logger.error("Failed to generate schema", e);
    }
  }

  private static final class GeneratorArguments {

    @Argument(index = 0, metaVar = "OUTPUT", usage = "output filename", required = true)
    private @NotNull String filename = "";
  }
}
