package org.shipstone.swagger.integration;

import io.swagger.jaxrs.config.BeanConfig;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.config.Log;
import org.ocpsoft.rewrite.servlet.config.*;

import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author François Robert
 */
public abstract class AbstractSwaggerURLRewriter extends HttpConfigurationProvider {

  public static final String DEFAULT_API_DOC_PATH = "/api-docs";
  public static final String DEFAULT_REST_APPLICATION_ROOT = "/api";
  public static final String DEFAULT_SYSTEM_SWAGGERUI_PROPERTIES = "swaggerui.properties";
  public static final String DEFAULT_SWAGGER_CONFIGURATION_FILE = "swagger-project.properties";
  private static final String DEFAULT_SWAGGER_UI_INDEX = "inside-docs/index.html";
  public static final String DEFAULT_HOST = "localhost:8080";

  private static final Logger LOGGER = Logger.getLogger(AbstractSwaggerURLRewriter.class);

  private static final String CONFIG_PREIXE = "swagger.";
  private static final String CONFIG_HOST = CONFIG_PREIXE + "host";
  private static final String CONFIG_RESTAPPLICATION_CLASSNAME = CONFIG_PREIXE + "restApplicationClassname";
  private static final String CONFIG_RESTAPPLICATION_PACKAGE = CONFIG_PREIXE + "restApplicationPackage";
  private static final String CONFIG_RESTAPPLICATION_PATH = CONFIG_PREIXE + "restApplicationPath";
  private static final String CONFIG_API_DOC_PATH = CONFIG_PREIXE + "apiDocPath";
  private static final String CONFIG_API_DOC_INDEX = CONFIG_PREIXE + "apiDocIndex";
  private static final String CONFIG_SWAGGER_ACTIVE = CONFIG_PREIXE + "active";

  public static final String EMPTY = "";

  /**
   * Systeme property store System swagger UI Integration file
   */
  private static final String CONFIG_SYSTEM_SWAGGERUI_PROPERTY = "systemPropertySwaggerUIName";

  /**
   * Webapp base path
   */
  private String basePath;

  /**
   * SwaggerUI sub path
   */
  private String apiDocPath = DEFAULT_API_DOC_PATH;

  /**
   * Rest API sub path
   */
  private String restApplicationRoot = DEFAULT_REST_APPLICATION_ROOT;

  /**
   * Swagger (core & UI) accessible
   */
  private boolean active = true;

  /**
   * Emplacement systeme du fichier de surcharge de configuration
   */
  private String systemPropertyForExternalConfigurationFilename = EMPTY;

  /**
   * Package de base de l'API Rest
   */
  private String resourcePackage = EMPTY;

  /**
   * resource configuration path
   */
  private String configurationFile;

  /**
   * resource private index file for swagger UI
   */
  private String apiDocIndex;
  private String host;

  @Override
  public Configuration getConfiguration(ServletContext servletContext) {
    swaggerConfiguration(servletContext.getContextPath());
    if (active) {
      swaggerCoreConfiguration();
      return ConfigurationBuilder.begin()
          .addRule()
          .when(
              Direction.isInbound()
                  .and(
                      Path.matches(apiDocPath + "/")
                  )
                  .or(
                      Path.matches(apiDocPath + "/index.html")
                  )
          )
          .perform(
              Lifecycle.abort()
                  .and(
                      StreamResources.from(apiDocIndex, "@#swaggerApiJsonPath#@", basePath + restApplicationRoot + "/")
                  )
                  .and(
                      Log.message(Logger.Level.INFO, "Client requested index - Stream from resources")
                  )
          )
          .addRule()
          .when(
              Direction.isInbound()
                  .and(
                      Path.matches(apiDocPath + "/{path}")
                  )
          )
          .perform(
              Forward.to("/swagger-ui-integration/{path}")
                  .and(
                      Log.message(Logger.Level.INFO, "Client requested path: {path}")
                  )
          )
          .where("path").matches(".*")
          ;
    } else {
      return ConfigurationBuilder.begin()
          .addRule()
          .when(
              Direction.isInbound()
                  .and(
                      Path.matches(apiDocPath + "{path}")
                  ).or(
                  Path.matches(restApplicationRoot + "swagger")
              )
          )
          .perform(
              SendStatus.error(403)
                  .and(
                      Log.message(Logger.Level.INFO, "Client requested swagger API access - no access allowed")
                  )
          )
          .where("path").matches(".*")
          ;
    }
  }

  private void swaggerConfiguration(final String basePath) {
    this.basePath = basePath;
    this.configurationFile = DEFAULT_SWAGGER_CONFIGURATION_FILE;
    swaggerConfigurationFromAnnotation();
    swaggerConfigurationFromResourceFile();
    swaggerConfigurationFromSystemFile();
  }

  private void swaggerConfigurationFromSystemFile() {
    if (systemPropertyForExternalConfigurationFilename != null && !EMPTY.equals(systemPropertyForExternalConfigurationFilename.trim())) {
      String configurationSystemFile = System.getProperty(systemPropertyForExternalConfigurationFilename);
      if (configurationSystemFile != null && Files.exists(Paths.get(configurationSystemFile))) {
        Properties systemProperties = new Properties();
        try {
          systemProperties.load(new FileInputStream(new File(configurationSystemFile)));
          readConfigurationProperties(systemProperties);
        } catch (IOException e) {
          LOGGER.warn("Some problems occured during during system configuration...");
        }
      }
    }
  }

  private void swaggerConfigurationFromResourceFile() {
    if (getClass().getClassLoader().getResource(DEFAULT_SWAGGER_CONFIGURATION_FILE) != null) {
      try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(DEFAULT_SWAGGER_CONFIGURATION_FILE)) {
        Properties configurationProperties = new Properties();
        configurationProperties.load(inputStream);
        readConfigurationProperties(configurationProperties);
      } catch (IOException e) {
        LOGGER.error("Error during configuration file access...");
      }

    }
  }

  private void readConfigurationProperties(Properties configurationProperties) {
    systemPropertyForExternalConfigurationFilename = configurationProperties.getProperty(CONFIG_SYSTEM_SWAGGERUI_PROPERTY, systemPropertyForExternalConfigurationFilename);
    host = configurationProperties.getProperty(CONFIG_HOST, host);
    String restApplicationClassname = configurationProperties.getProperty(CONFIG_RESTAPPLICATION_CLASSNAME, null);
    if (restApplicationClassname != null && !EMPTY.equals(restApplicationClassname.trim())) {
      try {
        Class<?> restApplicationClass = Class.forName(restApplicationClassname);
        extractRestApplicationRoot(restApplicationClass);
      } catch (ClassNotFoundException e) {
        LOGGER.warn("Rest Application class not found for " + restApplicationClassname);
      }
    } else  {
      restApplicationRoot = formatPath(configurationProperties.getProperty(CONFIG_RESTAPPLICATION_PATH, restApplicationRoot));
      resourcePackage = configurationProperties.getProperty(CONFIG_RESTAPPLICATION_PACKAGE, resourcePackage);
    }
    apiDocPath = formatPath(configurationProperties.getProperty(CONFIG_API_DOC_PATH, apiDocPath));
    apiDocIndex = configurationProperties.getProperty(CONFIG_API_DOC_INDEX, apiDocIndex);
    active = Boolean.valueOf(configurationProperties.getProperty(CONFIG_SWAGGER_ACTIVE, String.valueOf(active)));
  }

  private void swaggerConfigurationFromAnnotation() {
    SwaggerUIConfiguration swaggerUIConfiguration = getClass().getAnnotation(SwaggerUIConfiguration.class);
    if (swaggerUIConfiguration != null) {
      configurationFile = swaggerUIConfiguration.configurationFilename();
      systemPropertyForExternalConfigurationFilename = swaggerUIConfiguration.systemPropertyForExternalConfigurationFilename();
      host = swaggerUIConfiguration.host();
      Class<?> restApplicationClass = swaggerUIConfiguration.restApplicationClass();
      if (Void.class.isAssignableFrom(restApplicationClass)) {
        restApplicationRoot = formatPath(swaggerUIConfiguration.restApplicationPath());
        String restApplicationPackage = swaggerUIConfiguration.restApplicationPackage() == null ? EMPTY : swaggerUIConfiguration.restApplicationPackage();
        resourcePackage = EMPTY.equals(restApplicationPackage.trim()) ? getClass().getPackage().getName() : restApplicationPackage.trim();
      } else {
        extractRestApplicationRoot(restApplicationClass);
      }
      apiDocPath = formatPath(swaggerUIConfiguration.apiDocPath());
      apiDocIndex = (swaggerUIConfiguration.apiDocIndex() != null && !EMPTY.equals(swaggerUIConfiguration.apiDocIndex())) ? swaggerUIConfiguration.apiDocIndex() : DEFAULT_SWAGGER_UI_INDEX;
      active = swaggerUIConfiguration.active();
    }
  }

  private void extractRestApplicationRoot(Class<?> restApplicationClass) {
    ApplicationPath restApplicationPath = restApplicationClass.getAnnotation(ApplicationPath.class);
    restApplicationRoot = formatPath(restApplicationPath == null ? "" : restApplicationPath.value());
    resourcePackage = restApplicationClass.getPackage().getName();
  }

  private String formatPath(String value) {
    if (value.charAt(0) != '/') {
      value = "/" + value;
    }
    if (value.endsWith("/")) {
      value = value.substring(0, value.length() - 1);
    }
    return value;
  }

  private void swaggerCoreConfiguration() {
    BeanConfig beanConfig = new BeanConfig();
    beanConfig.setSchemes(new String[]{"http", "https"});
    beanConfig.setHost("localhost:8080");
    beanConfig.setBasePath(basePath + restApplicationRoot);
    beanConfig.setResourcePackage(resourcePackage);
    beanConfig.setScan(true);
  }

  @Override
  public int priority() {
    return 10;
  }

  public boolean isActive() {
    return active;
  }

  public String getApiDocPath() {
    return apiDocPath;
  }

  public String getBasePath() {
    return basePath;
  }

  public String getRestApplicationRoot() {
    return restApplicationRoot;
  }

  public String getSystemPropertyForExternalConfigurationFilename() {
    return systemPropertyForExternalConfigurationFilename;
  }

}
