package org.shipstone.swagger.integration.core.configuration.reader;

import org.shipstone.swagger.integration.core.configuration.Configuration;

/**
 * @author francois
 */
public interface SpecificConfigurationReader {

  /**
   * Read configuraton from specific source
   * @param configuration configuration
   * @return configuration read
   */
  Configuration readConfiguration(Configuration configuration);

}
