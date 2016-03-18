package org.shipstone.swagger.integration.core.configuration.reader;

import org.shipstone.swagger.integration.core.configuration.Configuration;
import org.shipstone.swagger.integration.core.configuration.DefaultConfigurationProvider;

import javax.servlet.ServletContext;

/**
 * @author francois
 */
public abstract class ConfigurationReader implements SpecificConfigurationReader, DefaultConfigurationProvider {

  @Override
  public Configuration readConfiguration(Configuration configuration, ServletContext servletContext) {
    return readConfigurationFrom(configuration == null ? Configuration.getDefault() : configuration, servletContext);
  }

  protected abstract Configuration readConfigurationFrom(Configuration configuration, ServletContext servletContext);

}
