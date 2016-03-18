package org.shipstone.swagger.integration.core.utils;

import static org.shipstone.swagger.integration.core.configuration.DefaultConfigurationProvider.EMPTY;

/**
 * @author francois
 */
public class StringUtils {


  public static boolean isNotEmpty(String string) {
    return !isEmpty(string);
  }

  public static boolean isEmpty(String string) {
    return string == null || EMPTY.equals(string.trim());
  }


}
