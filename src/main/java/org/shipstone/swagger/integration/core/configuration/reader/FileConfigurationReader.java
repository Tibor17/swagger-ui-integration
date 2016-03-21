package org.shipstone.swagger.integration.core.configuration.reader;

import org.shipstone.swagger.integration.core.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author François Robert
 */
public abstract class FileConfigurationReader extends ConfigurationReader {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileConfigurationReader.class);
  public static final String FILE_SWAGGER_PREFIXE = "swagger.";
  private Properties properties;

  @Override
  protected Configuration readConfigurationFrom(Configuration configuration, ServletContext servletContext) {
    if (couldReachConfigurationFile(configuration)) {
      configuration = readConfigurationFile(configuration);
    }
    return configuration;
  }

  private Configuration readConfigurationFile(Configuration configuration) {
    try (InputStream inputStream = getConfigurationFileStream(configuration)) {
      properties = buildPropertiesReader(inputStream);
      configuration.setActive(getProperty("active", configuration.isActive()));
      if (configuration.isActive()) {
        configuration.setSystemPropertyForExternalConfigurationFilename(getProperty("systemPropertyForExternalConfigurationFilename", configuration.getSystemPropertyForExternalConfigurationFilename()));
        configuration.setHost(getProperty("host", configuration.getHost()));
        configuration.setRestApplicationClass(getProperty("restApplicationClass", configuration.getRestApplicationClass()));
        configuration.setRestApplicationPackageAsRoot(getProperty("restApplicationPackageAsRoot", configuration.isRestApplicationPackageAsRoot()));
        configuration.setRestApplicationPath(getProperty("restApplicationPath", configuration.getRestApplicationPath()));
        configuration.setRestApplicationPackage(getProperty("restApplicationPackage", configuration.getRestApplicationPackage()));
        configuration.setApiDocPath(getProperty("apiDocPath", configuration.getApiDocPath()));
        configuration.setApiDocIndex(getProperty("apiDocIndex", configuration.getApiDocIndex()));
      }
    } catch (IOException e) {
      LOGGER.error("error during read configuration resources file");
    }
    return configuration;
  }

  private String getProperty(String propertyName, String defaultValue) {
    return properties.getProperty(getPropertyName(propertyName), defaultValue);
  }

  private String getPropertyName(String propertyName) {
    return FILE_SWAGGER_PREFIXE + propertyName;
  }

  private boolean getProperty(String propertyName, boolean defaultValue) {
    return Boolean.valueOf(properties.getProperty(getPropertyName(propertyName), String.valueOf(defaultValue)));
  }

  private Class<?> getProperty(String propertyName, Class<?> defaultValue) {
    String classname = properties.getProperty(getPropertyName(propertyName));
    if (classname != null && !EMPTY.equals(classname.trim())) {
      try {
        return Class.forName(classname);
      } catch (ClassNotFoundException e) {
        LOGGER.warn("Class not found");
      }
    }
    return defaultValue;
  }

  private Properties buildPropertiesReader(InputStream inputStream) throws IOException {
    Properties properties = new Properties();
    properties.load(inputStream);
    return properties;
  }

  protected abstract InputStream getConfigurationFileStream(Configuration configuration) throws IOException;

  protected abstract boolean couldReachConfigurationFile(Configuration configuration);
}
