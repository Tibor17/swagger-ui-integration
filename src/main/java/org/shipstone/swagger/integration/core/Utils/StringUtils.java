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

  public static  String setEndingSlash(String uri) {
    if (uri != null) {
      if (uri.startsWith("/")) {
        uri = uri.substring(1);
      }
      return uri.endsWith("/") ? uri : (uri + "/");
    } else {
      return uri;
    }
  }



}
