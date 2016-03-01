package org.shipstone.swagger.integration;

import org.junit.Test;
import org.shipstone.swagger.integration.tools.TestServletContext;

import javax.ws.rs.ApplicationPath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.shipstone.swagger.integration.AbstractSwaggerURLRewriter.DEFAULT_API_DOC_PATH;
import static org.shipstone.swagger.integration.AbstractSwaggerURLRewriter.DEFAULT_REST_APPLICATION_ROOT;
import static org.shipstone.swagger.integration.AbstractSwaggerURLRewriter.DEFAULT_SYSTEM_SWAGGERUI_PROPERTIES;
import static org.shipstone.swagger.integration.tools.TestServletContext.TEST_CONTEXT_PATH;

/**
 * @author francois
 */
public class AbstractSwaggerURLRewriterTest {

  @Test
  public void testDefaultSwaggerConfigurationByAnnotation() {
    DefaultSwaggerConfiguration defaultSwaggerConfiguration = new DefaultSwaggerConfiguration();
    defaultSwaggerConfiguration.getConfiguration(new TestServletContext());
    assertEquals(TEST_CONTEXT_PATH, defaultSwaggerConfiguration.getBasePath());
    assertEquals(DEFAULT_REST_APPLICATION_ROOT, defaultSwaggerConfiguration.getRestApplicationRoot());
    assertEquals(DEFAULT_API_DOC_PATH, defaultSwaggerConfiguration.getApiDocPath());
    assertTrue(defaultSwaggerConfiguration.isActive());
    assertEquals(DEFAULT_SYSTEM_SWAGGERUI_PROPERTIES, defaultSwaggerConfiguration.getSystemSwaggerUIProperties());
//    assertEquals(DEFAULT_SYSTEM_SWAGGERUI_PROPERTIES, defaultSwaggerConfiguration.sys);
  }

  @Test
  public void testRestAppSwaggerConfigurationByAnnotation() {
    RestAppSwaggerConfiguration restAppSwaggerConfiguration = new RestAppSwaggerConfiguration();
    restAppSwaggerConfiguration.getConfiguration(new TestServletContext());
    assertEquals(TEST_CONTEXT_PATH, restAppSwaggerConfiguration.getBasePath());
    assertEquals("/rest", restAppSwaggerConfiguration.getRestApplicationRoot());
    assertEquals("/apiRestDoc", restAppSwaggerConfiguration.getApiDocPath());
    assertFalse(restAppSwaggerConfiguration.isActive());
  }
}

@SwaggerUIConfiguration()
class DefaultSwaggerConfiguration extends AbstractSwaggerURLRewriter {
}

@SwaggerUIConfiguration(
    restApplication = TestRestApplication.class
    , apiDocPath = "apiRestDoc"
    , active = false
)
class RestAppSwaggerConfiguration extends AbstractSwaggerURLRewriter {
}

@ApplicationPath("rest")
class TestRestApplication {
}