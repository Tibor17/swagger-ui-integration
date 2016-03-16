package org.shipstone.swagger.integration.core.configuration.reader;

import org.shipstone.swagger.integration.core.configuration.Configuration;
import org.shipstone.swagger.integration.core.configuration.DefaultConfigurationProvider;

/**
 * @author francois
 */
public abstract class ConfigurationReader implements SpecificConfigurationReader, DefaultConfigurationProvider {

  @Override
  public Configuration readConfiguration(Configuration configuration) {
    return readConfigurationFrom(configuration == null ? getDefaultConfiguration() : configuration);
  }

  protected abstract Configuration readConfigurationFrom(Configuration configuration);

}
