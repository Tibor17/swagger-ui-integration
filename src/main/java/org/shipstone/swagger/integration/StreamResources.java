package org.shipstone.swagger.integration;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.config.Response;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Set;

/**
 * Allow operation for stream resource file to response
 *
 * @author Francois Robert
 */
public abstract class StreamResources extends HttpOperation implements Parameterized {

  public static final Logger log = Logger.getLogger(StreamResources.class);

  private final String resourceLocation;
  protected RegexParameterizedPatternBuilder target;

  public StreamResources(String resourceLocation) {
    this.target = new RegexParameterizedPatternBuilder(resourceLocation);
    this.resourceLocation = resourceLocation;
  }

  /**
   * Initialize response with file from resource
   *
   * @param resourceLocation resource location
   * @return StreamResource (HttpOperation)
   */
  public static StreamResources from(final String resourceLocation) {
    return new StreamResources(resourceLocation) {
      @Override
      public void performHttp(HttpServletRewrite httpServletRewrite, EvaluationContext evaluationContext) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream(resourceLocation))) {
          Response.write(bufferedInputStream).perform(httpServletRewrite, evaluationContext);
        } catch (IOException e) {
          throw new RewriteException("Error closing stream.", e);
        }
      }
    };
  }

  /**
   * Initialize response with file from resource
   *
   * @param resourceLocation resource location
   * @return StreamResource (HttpOperation)
   */
  public static StreamResources from(final String resourceLocation, final String targetInFile, final String replacementInFile) {
    return from(resourceLocation, targetInFile, replacementInFile, StandardCharsets.UTF_8);
  }

  /**
   * Initialize response with file from resource
   *
   * @param resourceLocation resource location
   * @return StreamResource (HttpOperation)
   */
  public static StreamResources from(final String resourceLocation, final String targetInFile, final String replacementInFile, final Charset charset) {
    return new StreamResources(resourceLocation) {
      @Override
      public void performHttp(HttpServletRewrite httpServletRewrite, EvaluationContext evaluationContext) {
        StringBuffer resourceFile = new StringBuffer();
        String systemLineSeparator = System.getProperty("line.separator").toString();
        InputStream resourceFileInputStream = getClass().getClassLoader().getResourceAsStream(resourceLocation);
        if (resourceFileInputStream != null) {
          Scanner resourceFileScanner = new Scanner(resourceFileInputStream);
          while (resourceFileScanner.hasNextLine()) {
            resourceFile.append(resourceFileScanner.nextLine().replace(targetInFile, replacementInFile)).append(systemLineSeparator);
          }
          try (InputStream responseInputStream = new ByteArrayInputStream(resourceFile.toString().getBytes(charset))) {
            Response.write(responseInputStream).perform(httpServletRewrite, evaluationContext);
          } catch (IOException e) {
            throw new RewriteException("Error closing stream.", e);
          }
        }
      }
    };
  }

  @Override
  public Set<String> getRequiredParameterNames() {
    return target.getRequiredParameterNames();
  }

  @Override
  public void setParameterStore(ParameterStore store) {
    target.setParameterStore(store);
  }
}
