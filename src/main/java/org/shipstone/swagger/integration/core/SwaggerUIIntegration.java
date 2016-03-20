package org.shipstone.swagger.integration.core;

import io.swagger.jaxrs.config.BeanConfig;
import org.shipstone.swagger.integration.core.configuration.Configuration;
import org.shipstone.swagger.integration.core.configuration.reader.AnnotationConfigurationReader;
import org.shipstone.swagger.integration.core.configuration.reader.ResourcesConfigurationReader;
import org.shipstone.swagger.integration.core.configuration.reader.SpecificConfigurationReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static org.shipstone.swagger.integration.core.utils.StringUtils.setEndingSlash;

/**
 * @author francois
 */
public class SwaggerUIIntegration implements Filter {

  private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerUIIntegration.class);
  private static final String SWAGGER_API_JSON_PATH_WILDCARD = "@#swaggerApiJsonPath#@";
  private static final String INDEX_HTML = "index.html";
  public static final String GET = "GET";
  private Configuration configuration = null;

  private boolean notForwarded = true;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    readConfigurations(filterConfig.getServletContext());
  }

  private void readConfigurations(ServletContext servletContext) {
    for (SpecificConfigurationReader configurationReader : fillConfigurationReader()) {
      configuration = configurationReader.readConfiguration(configuration, servletContext);
    }
  }

  private List<SpecificConfigurationReader> fillConfigurationReader() {
    List<SpecificConfigurationReader> configurationReaderList = new LinkedList<>();
    configurationReaderList.add(new AnnotationConfigurationReader());
    configurationReaderList.add(new ResourcesConfigurationReader());
    return configurationReaderList;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    if (notForwarded && configuration.isActive() && GET.equals(((HttpServletRequest) servletRequest).getMethod())) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
      checkHost(httpServletRequest);
      String path = httpServletRequest.getRequestURI();
      if (path.startsWith(configuration.getContextApiDocPath()) && notForwarded) {
        if (configuration.getContextApiDocPath().equals(path) || (configuration.getContextApiDocPath() + INDEX_HTML).equals(path)) {
          flushResponse(servletResponse, configuration.getApiDocIndex(), SWAGGER_API_JSON_PATH_WILDCARD, "/" + configuration.getContextPath() + configuration.getRestApplicationPath() + "/");
        } else {
          forward(path, servletRequest, servletResponse);
        }
      } else {
        filterChain.doFilter(servletRequest, servletResponse);
      }
    } else {
      if (!notForwarded) {
        notForwarded = true;
      }
      filterChain.doFilter(servletRequest, servletResponse);
    }
  }

  private void flushResponse(ServletResponse servletResponse, String apiDocIndex, String targetInFile, String replacementInFile) {
    flushResponse(servletResponse, apiDocIndex, targetInFile, replacementInFile, StandardCharsets.UTF_8);
  }

  private void flushResponse(ServletResponse servletResponse, String resourceLocation, String targetInFile, String replacementInFile, Charset charset) {
    try (OutputStream responseOutputStream = servletResponse.getOutputStream(); InputStream resourceFileInputStream = getClass().getClassLoader().getResourceAsStream(resourceLocation)) {
      String systemLineSeparator = System.getProperty("line.separator");
      if (resourceFileInputStream != null) {
        Scanner resourceFileScanner = new Scanner(resourceFileInputStream);
        while (resourceFileScanner.hasNextLine()) {
          responseOutputStream.write((resourceFileScanner.nextLine().replace(targetInFile, replacementInFile) + systemLineSeparator).getBytes());
        }
      }
      responseOutputStream.flush();
    } catch (IOException e) {
      LOGGER.error("Error during response stream processing", e);
    }

  }

  private void forward(String path, ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
    notForwarded = false;
    String targetPath = "/swagger-ui-integration/" + path.substring(configuration.getContextApiDocPath().length());
    RequestDispatcher requestDispatcher = servletRequest.getRequestDispatcher(targetPath);
    requestDispatcher.forward(servletRequest, servletResponse);
  }

  private void checkHost(HttpServletRequest httpServletRequest) {
    if (!configuration.isHostSet()) {
      configuration.setContextPath(setEndingSlash(httpServletRequest.getContextPath()));
      configuration.setHost(setEndingSlash(httpServletRequest.getRequestURL().substring(0, httpServletRequest.getRequestURL().length() - httpServletRequest.getRequestURI().length())));
      setSwaggerCoreConfigurationAndScan();
    }
  }

  private void setSwaggerCoreConfigurationAndScan() {
    BeanConfig beanConfig = new BeanConfig();
    beanConfig.setSchemes(new String[]{"http", "https"});
    if (configuration.getHost().startsWith("https")) {
      beanConfig.setHost(configuration.getHost().substring(8));
    } else {
      beanConfig.setHost(configuration.getHost().substring(7));
    }
    if (beanConfig.getHost().endsWith("/")) {
      beanConfig.setHost(beanConfig.getHost().substring(0, beanConfig.getHost().length() - 1));
    }
    beanConfig.setBasePath(configuration.getContextPath() + configuration.getRestApplicationPath());
    beanConfig.setResourcePackage(configuration.getRestApplicationPackage());
    beanConfig.setScan(true);
  }

  @Override
  public void destroy() {
  }
}
