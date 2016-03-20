package org.shipstone.swagger.integration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.shipstone.swagger.integration.core.configuration.DefaultConfigurationProvider.*;

/**
 * @author François Robert
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SwaggerUIConfiguration {

  /**
   * Default resource file store swagger-ui-integration configuration
   * @return filename
   */
  String configurationFilename() default DEFAULT_SWAGGER_CONFIGURATION_FILE;

  /**
   * property system name storing the full path to the external configuration file for swagger-ui-integration.
   *
   * @return system property name
   */
  String systemPropertyForExternalConfigurationFilename() default EMPTY;

  /**
   * Default host, used by swagger-core configuration for expose correct host in swagger json description.
   * @return host.
   */
  String host() default EMPTY;

  /**
   * use @ApplicationPath annoted class package as root for swagger introspection.
   *
   * by default true, but if no @ApplicationPath class found, default turn to false
   *
   * @return true / false
   */
  boolean restApplicationPackageAsRoot() default true;

  /**
   * Base package REST application - used only if the restApplicationClass was undefined or restApplicationPackageAsRoot set to false.
   *
   * @return package to scan by Swagger
   */
  String restApplicationPackage() default EMPTY;

  /**
   * Path de l'UI Swagger
   *
   * @return path
   */
  String apiDocPath() default DEFAULT_API_DOC_PATH;

  /**
   * default Index.html file replacement
   * @return resource index.html file replacement
   */
  String apiDocIndex() default DEFAULT_SWAGGER_UI_INDEX;

  /**
   * Active swagger (core and UI)
   *
   * @return swagger actif
   */
  boolean active() default true;

}
