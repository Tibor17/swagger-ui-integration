package org.shipstone.swagger.integration.core.configuration.reader;

import org.shipstone.swagger.integration.core.configuration.Configuration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author François Robert
 */
public class ExternalConfigurationReader extends FileConfigurationReader {
  @Override
  protected InputStream getConfigurationFileStream(Configuration configuration) throws IOException {
    String configurationSystemFile = System.getProperty(configuration.getSystemPropertyForExternalConfigurationFilename());
    return new FileInputStream(new File(configurationSystemFile));
  }

  @Override
  protected boolean couldReachConfigurationFile(Configuration configuration) {
    if (configuration.getSystemPropertyForExternalConfigurationFilename() != null && !EMPTY.equals(configuration.getSystemPropertyForExternalConfigurationFilename().trim())) {
      String configurationSystemFile = System.getProperty(configuration.getSystemPropertyForExternalConfigurationFilename());
      return (configurationSystemFile != null && Files.exists(Paths.get(configurationSystemFile)));
    }
    return false;
  }
}
