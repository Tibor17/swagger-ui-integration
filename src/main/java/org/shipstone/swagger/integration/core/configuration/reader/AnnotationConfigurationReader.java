package org.shipstone.swagger.integration.core.configuration.reader;

import org.shipstone.swagger.integration.annotation.SwaggerUIConfiguration;
import org.shipstone.swagger.integration.core.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import java.lang.annotation.Annotation;
import java.util.Map;

import static org.shipstone.swagger.integration.core.utils.StringUtils.isNotEmpty;

/**
 * @author francois
 */
public class AnnotationConfigurationReader extends ConfigurationReader {

  private static final Logger LOGGER = LoggerFactory.getLogger(ByteCodeAnnotationScanner.class);

  private ClassLoader classLoader;

  @Override
  protected Configuration readConfigurationFrom(Configuration configuration, ServletContext servletContext) {
    @SuppressWarnings("unchecked") Map<Class<? extends Annotation>, String> annotatedClassMap = new ByteCodeAnnotationScanner(servletContext, SwaggerUIConfiguration.class, ApplicationPath.class).get();
    if (annotatedClassMap.size() > 0) {
      classLoader = getClassLoader();
      return readConfiguration(annotatedClassMap, configuration);
    }
    return configuration;
  }

  private Configuration readConfiguration(Map<Class<? extends Annotation>, String> annotatedClassMap, Configuration configuration) {
    SwaggerUIConfiguration swaggerUIConfiguration = getAnnotationFrom(annotatedClassMap, SwaggerUIConfiguration.class);
    if (swaggerUIConfiguration != null) {
      applyConfigurationFromAnnotation(annotatedClassMap, configuration, swaggerUIConfiguration);
    } else {
      configuration.setActive(false);
    }
    return configuration;
  }

  private void applyConfigurationFromAnnotation(Map<Class<? extends Annotation>, String> annotatedClassMap, Configuration configuration, SwaggerUIConfiguration swaggerUIConfiguration) {
    configuration.setActive(swaggerUIConfiguration.active());
    configuration.setConfigurationFilename(swaggerUIConfiguration.configurationFilename());
    if (isNotEmpty(swaggerUIConfiguration.systemPropertyForExternalConfigurationFilename())) {
      configuration.setSystemPropertyForExternalConfigurationFilename(swaggerUIConfiguration.systemPropertyForExternalConfigurationFilename());
    }
    configuration.setHost(swaggerUIConfiguration.host());
    ApplicationPath applicationPath = getAnnotationFrom(annotatedClassMap, ApplicationPath.class);
    if (applicationPath != null) {
      try {
        configuration.setRestApplicationClass(classLoader.loadClass(annotatedClassMap.get(ApplicationPath.class)));
        configuration.setRestApplicationPackageAsRoot(swaggerUIConfiguration.restApplicationPackageAsRoot());
        if (configuration.isRestApplicationPackageAsRoot()) {
          configuration.setRestApplicationPackage(configuration.getRestApplicationClass().getPackage().getName());
        }
      } catch (ClassNotFoundException e) {
        configuration.setRestApplicationClass(null);
      }
    }
    configuration.setRestApplicationPath(applicationPath.value());
    configuration.setApiDocPath(swaggerUIConfiguration.apiDocPath());
    configuration.setApiDocIndex(swaggerUIConfiguration.apiDocIndex());
  }

  private <A extends Annotation> A getAnnotationFrom(Map<Class<? extends Annotation>, String> annotatedClassMap, Class<A> annotationClass) {
    if (annotatedClassMap.containsKey(annotationClass)) {
      try {
        Class<?> cLassAnnotated = classLoader.loadClass(annotatedClassMap.get(annotationClass));
        return cLassAnnotated.getAnnotation(annotationClass);
      } catch (ClassNotFoundException e) {
        LOGGER.warn("CLass not found : " + annotatedClassMap.get(annotationClass), e);
      }
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("no class bearing the annotation " + annotationClass.getSimpleName());
    }
    return null;
  }

  private ClassLoader getClassLoader() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader == null) {
      classLoader = getClass().getClassLoader();
    }
    return classLoader;
  }

}
