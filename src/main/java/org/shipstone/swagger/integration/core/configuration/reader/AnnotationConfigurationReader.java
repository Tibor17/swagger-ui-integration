package org.shipstone.swagger.integration.core.configuration.reader;

import org.shipstone.swagger.integration.core.configuration.Configuration;

import javax.servlet.ServletContext;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author francois
 */
public class AnnotationConfigurationReader extends ConfigurationReader {

  @Override
  protected Configuration readConfigurationFrom(Configuration configuration, ServletContext servletContext) {
    Map<Class<? extends Annotation>, String> annotatedClassMap = new ByteCodeAnnotationScanner(servletContext).get();
    return null;
  }

}
