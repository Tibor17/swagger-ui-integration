package org.shipstone.swagger.integration.core.configuration.reader;

import org.shipstone.swagger.integration.annotation.SwaggerUIConfiguration;
import org.shipstone.swagger.integration.core.configuration.Configuration;

import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author francois
 */
public class AnnotationConfigurationReader extends ConfigurationReader {

  @Override
  protected Configuration readConfigurationFrom(Configuration configuration, ServletContext servletContext) {
    Map<Class<? extends Annotation>, String> annotatedClassMap = new ByteCodeAnnotationScanner(servletContext, SwaggerUIConfiguration.class, ApplicationPath.class).get();
    if (annotatedClassMap.size() > 0) {

    }
    return configuration;
  }

}
