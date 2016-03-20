package org.shipstone.swagger.integration.core.configuration.reader;

import org.shipstone.swagger.integration.core.configuration.Configuration;

import java.io.InputStream;

/**
 * @author François Robert
 */
public class ExternalConfigurationReader extends FileConfigurationReader {
  @Override
  protected InputStream getConfigurationFileStream(Configuration configuration) {
    return null;
  }

  @Override
  protected boolean couldReachConfigurationFile(Configuration configuration) {
    return false;
  }
}
