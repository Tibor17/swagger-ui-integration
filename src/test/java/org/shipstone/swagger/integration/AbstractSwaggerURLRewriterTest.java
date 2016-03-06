package org.shipstone.swagger.integration;

import org.junit.Test;
import org.shipstone.swagger.integration.tools.TestServletContext;

import javax.ws.rs.ApplicationPath;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;
import static org.shipstone.swagger.integration.AbstractSwaggerURLRewriter.*;
import static org.shipstone.swagger.integration.tools.TestServletContext.TEST_CONTEXT_PATH;

/**
 * @author francois
 */
public class AbstractSwaggerURLRewriterTest {

  @Test
  public void testDefaultSwaggerConfigurationByAnnotation() {
    System.setProperty(AbstractSwaggerURLRewriter.DEFAULT_SYSTEM_SWAGGERUI_PROPERTIES, "");
    DefaultSwaggerConfiguration defaultSwaggerConfiguration = new DefaultSwaggerConfiguration();
    defaultSwaggerConfiguration.getConfiguration(new TestServletContext());
    assertEquals(TEST_CONTEXT_PATH, defaultSwaggerConfiguration.getBasePath());
    assertEquals(DEFAULT_REST_APPLICATION_ROOT, defaultSwaggerConfiguration.getRestApplicationRoot());
    assertEquals(DEFAULT_API_DOC_PATH, defaultSwaggerConfiguration.getApiDocPath());
    assertTrue(defaultSwaggerConfiguration.isActive());
    assertEquals(EMPTY, defaultSwaggerConfiguration.getSystemPropertyForExternalConfigurationFilename());
  }

  @Test
  public void testRestAppSwaggerConfigurationByAnnotation() {
    System.setProperty(AbstractSwaggerURLRewriter.DEFAULT_SYSTEM_SWAGGERUI_PROPERTIES, "");
    RestAppSwaggerConfiguration restAppSwaggerConfiguration = new RestAppSwaggerConfiguration();
    restAppSwaggerConfiguration.getConfiguration(new TestServletContext());
    assertEquals(TEST_CONTEXT_PATH, restAppSwaggerConfiguration.getBasePath());
    assertEquals("/rest", restAppSwaggerConfiguration.getRestApplicationRoot());
    assertEquals("/apiRestDoc", restAppSwaggerConfiguration.getApiDocPath());
    assertFalse(restAppSwaggerConfiguration.isActive());
  }

  @Test
  public void ÇtestSystemSwaggerConfigurationByAnnotation() {
    Path path = Paths.get("swaggerUIConfig.properties");
    System.setProperty("systemUIConfig.properties", path.toAbsolutePath().toString());
    RestAppSystemSwaggerConfiguration swaggerConfiguration = new RestAppSystemSwaggerConfiguration();
    swaggerConfiguration.getConfiguration(new TestServletContext());
    assertEquals("systemUIConfig.properties", swaggerConfiguration.getSystemPropertyForExternalConfigurationFilename());
    assertEquals("/systemSwaggerApiDocPath", swaggerConfiguration.getApiDocPath());
    assertFalse(swaggerConfiguration.isActive());
  }

}

@SwaggerUIConfiguration()
class DefaultSwaggerConfiguration extends AbstractSwaggerURLRewriter {
}

@SwaggerUIConfiguration(
    restApplicationClass = TestRestApplication.class
    , apiDocPath = "apiRestDoc"
    , active = false
)
class RestAppSwaggerConfiguration extends AbstractSwaggerURLRewriter {
}

@SwaggerUIConfiguration(
    restApplicationClass = TestRestApplication.class
    , systemPropertyForExternalConfigurationFilename = "systemUIConfig.properties"
)
class RestAppSystemSwaggerConfiguration extends AbstractSwaggerURLRewriter {
}

@ApplicationPath("rest")
class TestRestApplication {
}