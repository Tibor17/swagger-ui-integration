package org.shipstone.swagger.integration.core;

import org.shipstone.swagger.integration.core.configuration.Configuration;
import org.shipstone.swagger.integration.core.configuration.reader.AnnotationConfigurationReader;
import org.shipstone.swagger.integration.core.configuration.reader.SpecificConfigurationReader;

import javax.servlet.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author francois
 */
public class SwaggerUIIntegration implements Filter {

  private Configuration configuration = null;

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
    return configurationReaderList;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

  }

  @Override
  public void destroy() {

  }
}
