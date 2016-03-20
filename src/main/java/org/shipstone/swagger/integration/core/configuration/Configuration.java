package org.shipstone.swagger.integration.core.configuration;

import java.util.Objects;

import static org.shipstone.swagger.integration.core.configuration.DefaultConfigurationProvider.*;

/**
 * @author francois robert
 */
public class Configuration {

  private boolean hostSet;
  private String contextPath;
  private String contextApiDocPath;

  public static Configuration getDefault() {
    return new Configuration(DEFAULT_SWAGGER_CONFIGURATION_FILE, null, DEFAULT_HOST, null, DEFAULT_REST_APPLICATION_ROOT, null, DEFAULT_API_DOC_PATH, DEFAULT_SWAGGER_UI_INDEX, false, true);
  }

  /**
   * external file store swagger-ui-integration configuration
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
   * use @ApplicationPath annoted class package as root for swagger introspection.
   */
  private boolean restApplicationPackageAsRoot;

  /**
   * Base path for REST application - used only if the restApplicationClass was undefined
   */
  private String restApplicationPath;

  /**
   * Base package REST application - used only if the restApplicationClass was undefined or restApplicationPackageAsRoot set to false.
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

  /**
   * Constructor
   * @param configuration configuration inherited
   */
  public Configuration(Configuration configuration) {
    this.configurationFilename = configuration.configurationFilename;
    this.systemPropertyForExternalConfigurationFilename = configuration.systemPropertyForExternalConfigurationFilename;
    this.host = configuration.host;
    this.restApplicationClass = configuration.restApplicationClass;
    this.restApplicationPath = configuration.restApplicationPath;
    this.restApplicationPackage = configuration.restApplicationPackage;
    this.apiDocPath = configuration.apiDocPath;
    this.apiDocIndex = configuration.apiDocIndex;
    this.active = configuration.active;
    this.restApplicationPackageAsRoot = configuration.restApplicationPackageAsRoot;
  }

  public Configuration(
      String configurationFilename
      , String systemPropertyForExternalConfigurationFilename
      , String host
      , Class<?> restApplicationClass
      , String restApplicationPath
      , String restApplicationPackage
      , String apiDocPath
      , String apiDocIndex
      , boolean active
      , boolean useRestApplicationPackageAsRoot
  ) {
    this.configurationFilename = configurationFilename;
    this.systemPropertyForExternalConfigurationFilename = systemPropertyForExternalConfigurationFilename;
    this.host = host;
    this.restApplicationClass = restApplicationClass;
    this.restApplicationPath = restApplicationPath;
    this.restApplicationPackage = restApplicationPackage;
    this.apiDocPath = apiDocPath;
    this.apiDocIndex = apiDocIndex;
    this.active = active;
    this.restApplicationPackageAsRoot = useRestApplicationPackageAsRoot;
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
    hostSet = host != null && !"".equals(host.trim());
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

  public boolean isRestApplicationPackageAsRoot() {
    return restApplicationPackageAsRoot;
  }

  public void setRestApplicationPackageAsRoot(boolean useRestApplicationPackageAsRoot) {
    this.restApplicationPackageAsRoot = useRestApplicationPackageAsRoot;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Configuration)) return false;
    Configuration that = (Configuration) o;
    return isRestApplicationPackageAsRoot() == that.isRestApplicationPackageAsRoot() &&
        isActive() == that.isActive() &&
        Objects.equals(getConfigurationFilename(), that.getConfigurationFilename()) &&
        Objects.equals(getSystemPropertyForExternalConfigurationFilename(), that.getSystemPropertyForExternalConfigurationFilename()) &&
        Objects.equals(getHost(), that.getHost()) &&
        Objects.equals(getRestApplicationClass(), that.getRestApplicationClass()) &&
        Objects.equals(getRestApplicationPath(), that.getRestApplicationPath()) &&
        Objects.equals(getRestApplicationPackage(), that.getRestApplicationPackage()) &&
        Objects.equals(getApiDocPath(), that.getApiDocPath()) &&
        Objects.equals(getApiDocIndex(), that.getApiDocIndex());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getConfigurationFilename(), getSystemPropertyForExternalConfigurationFilename(), getHost(), getRestApplicationClass(), isRestApplicationPackageAsRoot(), getRestApplicationPath(), getRestApplicationPackage(), getApiDocPath(), getApiDocIndex(), isActive());
  }

  @Override
  public String toString() {
    return "Configuration{" +
        "active=" + active +
        ", apiDocPath='" + apiDocPath + '\'' +
        ", apiDocIndex='" + apiDocIndex + '\'' +
        ", restApplicationClass=" + restApplicationClass +
        ", restApplicationPackageAsRoot=" + restApplicationPackageAsRoot +
        ", restApplicationPackage='" + restApplicationPackage + '\'' +
        '}';
  }

  public boolean isHostSet() {
    return hostSet;
  }

  public void setHostSet(boolean hostSet) {
    this.hostSet = hostSet;
  }

  public void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }

  public String getContextPath() {
    return contextPath;
  }

  public String getContextApiDocPath() {
    if (contextApiDocPath == null) {
      contextApiDocPath = "/" + contextPath + apiDocPath;
    }
    return contextApiDocPath;
  }

  public void setContextApiDocPath(String contextApiDocPath) {
    this.contextApiDocPath = contextApiDocPath;
  }
}
