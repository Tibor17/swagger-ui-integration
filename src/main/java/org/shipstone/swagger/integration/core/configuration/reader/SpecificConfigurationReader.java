package org.shipstone.swagger.integration.core.configuration.reader;

import org.shipstone.swagger.integration.core.configuration.Configuration;

import javax.servlet.ServletContext;

/**
 * @author francois
 */
public interface SpecificConfigurationReader {

  /**
   * Read configuraton from specific source
   * @param configuration configuration
   * @param servletContext servlet context
   * @return configuration read
   */
  Configuration readConfiguration(Configuration configuration, ServletContext servletContext);

}
