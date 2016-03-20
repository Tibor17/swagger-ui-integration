package org.shipstone.swagger.integration.core.configuration.reader;

import org.shipstone.swagger.integration.core.configuration.Configuration;

import java.io.InputStream;

/**
 * @author François Robert
 */
public class ResourcesConfigurationReader extends FileConfigurationReader {

  public static final String DEFAULT_SWAGGER_CONFIGURATION_FILE = "swagger-project.properties";

  private ClassLoader classLoader;

  @Override
  protected InputStream getConfigurationFileStream(Configuration configuration) {
    return classLoader.getResourceAsStream(getConfigurationFilename(configuration));
  }

  private String getConfigurationFilename(Configuration configuration) {
    return configuration.getConfigurationFilename() == null ? DEFAULT_SWAGGER_CONFIGURATION_FILE : configuration.getConfigurationFilename();
  }

  @Override
  protected boolean couldReachConfigurationFile(Configuration configuration) {
    return (classLoader = getClassLoader()) != null
        && classLoader.getResource(getConfigurationFilename(configuration)) != null;
  }
}
