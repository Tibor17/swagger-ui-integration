package org.shipstone.swagger.integration.core.configuration.reader;

import org.shipstone.swagger.integration.core.configuration.Configuration;
import org.shipstone.swagger.integration.core.configuration.DefaultConfigurationProvider;
import org.shipstone.swagger.integration.core.utils.StringUtils;

import javax.servlet.ServletContext;

import static org.shipstone.swagger.integration.core.utils.StringUtils.setEndingSlash;

/**
 * @author francois
 */
public abstract class ConfigurationReader implements SpecificConfigurationReader, DefaultConfigurationProvider {

  @Override
  public Configuration readConfiguration(Configuration configuration, ServletContext servletContext) {
    configuration = readConfigurationFrom(configuration == null ? Configuration.getDefault() : configuration, servletContext);
    updatePaths(configuration);
    return configuration;
  }

  public void updatePaths(Configuration configuration) {
    configuration.setApiDocPath(setEndingSlash(configuration.getApiDocPath()));
  }

  protected abstract Configuration readConfigurationFrom(Configuration configuration, ServletContext servletContext);

}
