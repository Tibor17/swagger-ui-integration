package org.shipstone.swagger.integration.core.configuration.reader;

import org.shipstone.swagger.integration.core.configuration.Configuration;
import org.shipstone.swagger.integration.core.configuration.DefaultConfigurationProvider;

import javax.servlet.ServletContext;

import static org.shipstone.swagger.integration.core.tools.StringUtils.setEndingSlash;

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

  /**
   * update paths
   * @param configuration configuration
   */
  public void updatePaths(Configuration configuration) {
    configuration.setApiDocPath(setEndingSlash(configuration.getApiDocPath()));
  }

  protected abstract Configuration readConfigurationFrom(Configuration configuration, ServletContext servletContext);

  protected ClassLoader getClassLoader() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader == null) {
      classLoader = getClass().getClassLoader();
    }
    return classLoader;
  }
}
