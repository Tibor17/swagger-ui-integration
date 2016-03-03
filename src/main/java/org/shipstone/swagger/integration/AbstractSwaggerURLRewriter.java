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


  private static final Logger LOGGER = Logger.getLogger(AbstractSwaggerURLRewriter.class);

  private static final String PROJECT_SWAGGER_CONFIGURATION_FILE = "swagger-project.properties";
  private static final String CONFIG_API_DOC_PATH = "swaggerApiDocPath";
  private static final String CONFIG_API_SWAGGER_ENDPOINT = "swaggerApiEndpoint";
  private static final String CONFIG_SWAGGER_ACTIVE = "swaggerActive";
  private static final String CONFIG_RESTAPPLICATION_CLASSNAME = "restApplicationClass";

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
  private String systemSwaggerUIProperties = EMPTY;

  /**
   * Package de base de l'API Rest
   */
  private String resourcePackage = EMPTY;

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
                      StreamResources.from("inside-docs/index.html", "@#swaggerApiJsonPath#@", basePath + restApplicationRoot + "/")
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
                      Log.message(Logger.Level.INFO, "Client requested path: {path} - Forward to /swagger-ui-integration/{path}")
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
    swaggerConfigurationFromAnnotation();
    swaggerConfigurationFromResourceFile();
    swaggerConfigurationFromSystemFile();
  }

  private void swaggerConfigurationFromSystemFile() {
    if (systemSwaggerUIProperties != null && !EMPTY.equals(systemSwaggerUIProperties.trim())) {
      String configurationSystemFile = System.getProperty(systemSwaggerUIProperties);
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
    if (getClass().getClassLoader().getResource(PROJECT_SWAGGER_CONFIGURATION_FILE) != null) {
      try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROJECT_SWAGGER_CONFIGURATION_FILE)) {
        Properties configurationProperties = new Properties();
        configurationProperties.load(inputStream);
        readConfigurationProperties(configurationProperties);
      } catch (IOException e) {
        LOGGER.error("Error during configuration file access...");
      }

    }
  }

  private void readConfigurationProperties(Properties configurationProperties) {
    String restApplicationClassname = configurationProperties.getProperty(CONFIG_RESTAPPLICATION_CLASSNAME, null);
    if (restApplicationClassname != null && !EMPTY.equals(restApplicationClassname.trim())) {
      try {
        Class<?> restApplicationClass = Class.forName(restApplicationClassname);
        extractRestApplicationRoot(restApplicationClass);
      } catch (ClassNotFoundException e) {
        LOGGER.warn("Rest Application class not found for " + restApplicationClassname);
      }
    }
    active = Boolean.valueOf(configurationProperties.getProperty(CONFIG_SWAGGER_ACTIVE, String.valueOf(active)));
    apiDocPath = formatPath(configurationProperties.getProperty(CONFIG_API_DOC_PATH, apiDocPath));
    restApplicationRoot = formatPath(configurationProperties.getProperty(CONFIG_API_SWAGGER_ENDPOINT, restApplicationRoot));
    systemSwaggerUIProperties = configurationProperties.getProperty(CONFIG_SYSTEM_SWAGGERUI_PROPERTY, systemSwaggerUIProperties);
  }

  private void swaggerConfigurationFromAnnotation() {
    SwaggerUIConfiguration swaggerUIConfiguration = getClass().getAnnotation(SwaggerUIConfiguration.class);
    if (swaggerUIConfiguration != null) {
      Class<?> restApplicationClass = swaggerUIConfiguration.restApplication();
      if (Void.class.isAssignableFrom(restApplicationClass)) {
        restApplicationRoot = formatPath(swaggerUIConfiguration.restApplicationPath());
      } else {
        extractRestApplicationRoot(restApplicationClass);
      }
      apiDocPath = formatPath(swaggerUIConfiguration.apiDocPath());
      active = swaggerUIConfiguration.active();
      systemSwaggerUIProperties = swaggerUIConfiguration.externalConfigurationFile();
    }
  }

  private void extractRestApplicationRoot(Class<?> restApplicationClass) {
    ApplicationPath restApplicationPath = restApplicationClass.getAnnotation(ApplicationPath.class);
    restApplicationRoot = formatPath(restApplicationPath.value());
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

  public String getSystemSwaggerUIProperties() {
    return systemSwaggerUIProperties;
  }

}
