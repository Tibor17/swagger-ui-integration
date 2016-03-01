package org.shipstone.swagger.integration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.shipstone.swagger.integration.AbstractSwaggerURLRewriter.*;

/**
 * @author François Robert
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SwaggerUIConfiguration {

  /**
   * Classe de l'application JaxRs.
   *
   * @return classe
   */
  Class<?> restApplication() default Void.class;

  /**
   * path de l'application REST, utilisé seulement si la classe de l'application REST n'est pas fourni
   * Valeur par defaut : api
   *
   * @return path
   */
  String restApplicationPath() default DEFAULT_REST_APPLICATION_ROOT;

  /**
   * Path de l'UI Swagger
   *
   * @return path
   */
  String apiDocPath() default DEFAULT_API_DOC_PATH;

  /**
   * Active swagger (core & UI)
   *
   * @return swagger actif
   */
  boolean active() default true;

  /**
   * Parametre systeme indiquant le path pour le fichier de surcharge de la configuration
   *
   * @return propriete systeme
   */
  String externalConfigurationFile() default DEFAULT_SYSTEM_SWAGGERUI_PROPERTIES;

}
