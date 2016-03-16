package org.shipstone.swagger.integration.core;

import java.util.Objects;

/**
 * @author francois robert
 */
public class Configuration {

  /**
   * resource file store swagger-ui-integration configuration
   */
  private String configurationFilename;

  /**
   * property system name storing the full path to the external configuration file for swagger-ui-integration.
   */
  private String systemPropertyForExternalConfigurationFilename;

  /**
   * Webapp host, used by swagger-core configuration for expose correct host in swagger json description.
   */
  private String host;

  /**
   * Class use @ApplicationPath JAXRS annotation. Used to defined restApplicationPath and package to scan by swagger introspection.
   */
  private Class<?> restApplicationClass;

  /**
   * Base path for REST application - used only if the restApplicationClass was undefined
   */
  private String restApplicationPath;

  /**
   * Base package REST application - used only if the restApplicationClass was undefined.
   */
  private String restApplicationPackage;

  /**
   * UI Swagger base path
   */
  private String apiDocPath;

  /**
   * Index.html file replacement
   */
  private String apiDocIndex;

  /**
   * Active swagger (core and UI)
   */
  private boolean active;

  /**
   * Constructor
   */
  public Configuration() {
  }

  public Configuration(String configurationFilename, String systemPropertyForExternalConfigurationFilename, String host, Class<?> restApplicationClass, String restApplicationPath, String restApplicationPackage, String apiDocPath, String apiDocIndex, boolean active) {
    this.configurationFilename = configurationFilename;
    this.systemPropertyForExternalConfigurationFilename = systemPropertyForExternalConfigurationFilename;
    this.host = host;
    this.restApplicationClass = restApplicationClass;
    this.restApplicationPath = restApplicationPath;
    this.restApplicationPackage = restApplicationPackage;
    this.apiDocPath = apiDocPath;
    this.apiDocIndex = apiDocIndex;
    this.active = active;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public String getApiDocIndex() {
    return apiDocIndex;
  }

  public void setApiDocIndex(String apiDocIndex) {
    this.apiDocIndex = apiDocIndex;
  }

  public String getApiDocPath() {
    return apiDocPath;
  }

  public void setApiDocPath(String apiDocPath) {
    this.apiDocPath = apiDocPath;
  }

  public String getConfigurationFilename() {
    return configurationFilename;
  }

  public void setConfigurationFilename(String configurationFilename) {
    this.configurationFilename = configurationFilename;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Class<?> getRestApplicationClass() {
    return restApplicationClass;
  }

  public void setRestApplicationClass(Class<?> restApplicationClass) {
    this.restApplicationClass = restApplicationClass;
  }

  public String getRestApplicationPackage() {
    return restApplicationPackage;
  }

  public void setRestApplicationPackage(String restApplicationPackage) {
    this.restApplicationPackage = restApplicationPackage;
  }

  public String getRestApplicationPath() {
    return restApplicationPath;
  }

  public void setRestApplicationPath(String restApplicationPath) {
    this.restApplicationPath = restApplicationPath;
  }

  public String getSystemPropertyForExternalConfigurationFilename() {
    return systemPropertyForExternalConfigurationFilename;
  }

  public void setSystemPropertyForExternalConfigurationFilename(String systemPropertyForExternalConfigurationFilename) {
    this.systemPropertyForExternalConfigurationFilename = systemPropertyForExternalConfigurationFilename;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Configuration)) return false;
    Configuration that = (Configuration) o;
    return isActive() == that.isActive() &&
        Objects.equals(getConfigurationFilename(), that.getConfigurationFilename()) &&
        Objects.equals(getSystemPropertyForExternalConfigurationFilename(), that.getSystemPropertyForExternalConfigurationFilename()) &&
        Objects.equals(getRestApplicationClass(), that.getRestApplicationClass()) &&
        Objects.equals(getRestApplicationPath(), that.getRestApplicationPath()) &&
        Objects.equals(getRestApplicationPackage(), that.getRestApplicationPackage()) &&
        Objects.equals(getApiDocPath(), that.getApiDocPath()) &&
        Objects.equals(getApiDocIndex(), that.getApiDocIndex());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getConfigurationFilename(), getSystemPropertyForExternalConfigurationFilename(), getRestApplicationClass(), getRestApplicationPath(), getRestApplicationPackage(), getApiDocPath(), getApiDocIndex(), isActive());
  }

  @Override
  public String toString() {
    return "SwaggerConfiguration{" +
        "active=" + active +
        ", apiDocPath='" + apiDocPath + '\'' +
        ", restApplicationClass=" + restApplicationClass +
        ", restApplicationPath='" + restApplicationPath + '\'' +
        ", restApplicationPackage='" + restApplicationPackage + '\'' +
        '}';
  }
}
