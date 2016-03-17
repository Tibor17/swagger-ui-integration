package org.shipstone.swagger.integration.core.configuration.reader;

import org.shipstone.swagger.integration.core.configuration.Configuration;
import org.shipstone.swagger.integration.core.configuration.DefaultConfigurationProvider;

import javax.servlet.ServletContext;

/**
 * @author francois
 */
public abstract class ConfigurationReader implements SpecificConfigurationReader, DefaultConfigurationProvider {

  /**
   * Provide default configuration with default values
   * @return default configuration
   */
  private Configuration getDefaultConfiguration() {
    return new Configuration(DEFAULT_SWAGGER_CONFIGURATION_FILE, null, DEFAULT_HOST, null, DEFAULT_REST_APPLICATION_ROOT, null, DEFAULT_API_DOC_PATH, DEFAULT_SWAGGER_UI_INDEX, true);
  }

  @Override
  public Configuration readConfiguration(Configuration configuration, ServletContext servletContext) {
    return readConfigurationFrom(configuration == null ? getDefaultConfiguration() : configuration, servletContext);
  }

  protected abstract Configuration readConfigurationFrom(Configuration configuration, ServletContext servletContext);

}
