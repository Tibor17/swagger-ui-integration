package org.shipstone.swagger.integration.core.configuration.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author francois robert
 */
public class ByteCodeAnnotationScanner {

  private static final Logger LOGGER = LoggerFactory.getLogger(ByteCodeAnnotationScanner.class);

  public static final String WEB_INF_CLASSES_FOLDER = "/WEB-INF/classes/";
  public static final String CLASS_EXTENSION = ".class";

  private final static int CONSTANT_Class = 7;
  private final static int CONSTANT_Fieldref = 9;
  private final static int CONSTANT_Methodref = 10;
  private final static int CONSTANT_InterfaceMethodref = 11;
  private final static int CONSTANT_String = 8;
  private final static int CONSTANT_Integer = 3;
  private final static int CONSTANT_Float = 4;
  private final static int CONSTANT_Long = 5;
  private final static int CONSTANT_Double = 6;
  private final static int CONSTANT_NameAndType = 12;
  private final static int CONSTANT_Utf8 = 1;

  private ServletContext servletContext;
  private String basePackage;
  private String basePath;

  private Map<String, Class<? extends Annotation>> seekingAnnotationMap;
  private Map<Class<? extends Annotation>, String> annotatedClassname;

  public ByteCodeAnnotationScanner(ServletContext servletContext, Class<? extends Annotation>... annotationClasses) {
    this(servletContext, null, annotationClasses);
  }

  public ByteCodeAnnotationScanner(ServletContext servletContext, String basePackage, Class<? extends Annotation>... annotationClasses) {
    fillAnnotionInformation(annotationClasses);
    this.servletContext = servletContext;
    if (basePackage != null && !basePackage.endsWith(".class")) {
      this.basePackage = basePackage;
      this.basePath = WEB_INF_CLASSES_FOLDER + basePackage.replace('.', '/');
    } else {
      this.basePackage = WEB_INF_CLASSES_FOLDER;
    }
  }

  private void fillAnnotionInformation(Class<? extends Annotation>... annotationClasses) {
    seekingAnnotationMap = new HashMap<>();
    annotatedClassname = new HashMap<>();
    for (Class<? extends Annotation> aClass : annotationClasses) {
      seekingAnnotationMap.put("L" + aClass.getName().replace('.', '/'), aClass);
    }
  }

  /**
   * Retrieve the first class use annotations
   *
   * @return classname map
   */
  public Map<Class<? extends Annotation>, String> get() {
    try {
      URL basePathUrl = new URL(basePackage);
      processFolder(basePath);
    } catch (MalformedURLException e) {
      LOGGER.warn("Error during process base packahe URL", e);
    }
    return annotatedClassname;
  }

  private void processFolder(String basePath) {
    Iterator<String> subPathIterator = getsubPathIterator(basePath);
    while (subPathIterator.hasNext() && seekingAnnotationMap.size() > 0) {
      String subPath = subPathIterator.next();
      if (subPath.endsWith("/")) {
        processFolder(subPath);
      } else if (subPath.endsWith(CLASS_EXTENSION)) {
        processClass(subPath);
      }
    }
  }

  private void processClass(String classPath) {
    String classname = getClassnameFrom(classPath);
    InputStream inputStream = null;
    try {
      processClass(inputStream, getClassnameFrom(classPath));
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          LOGGER.warn("Error during stream processing");
        }
      }
    }
  }

  private void processClass(InputStream inputStream, String classname) {
    if (inputStream == null) {
      return;
    }
    DataInputStream dataInputStream = new DataInputStream(inputStream);
    try {
      if (dataInputStream.readInt() == 0xCAFEBABE) {
        dataInputStream.readUnsignedShort();
        if (dataInputStream.readUnsignedShort() >= 49) {
          processClassBytecode(classname, dataInputStream);
        }
      }
    } catch (IOException e) {
      LOGGER.error("Erreur lors de la lecture du flux de la classe");
    }
  }

  private void processClassBytecode(String classname, DataInputStream dataInputStream) throws IOException {
    int constantPoolEntries = dataInputStream.readUnsignedShort() - 1;
    for (int idxPoolEntry = 0; idxPoolEntry < constantPoolEntries; idxPoolEntry++) {
      int bytecodeId = dataInputStream.readUnsignedByte();
      switch (bytecodeId) {
        case CONSTANT_Class:
          dataInputStream.readUnsignedShort();
          break;
        case CONSTANT_Fieldref:
        case CONSTANT_Methodref:
        case CONSTANT_InterfaceMethodref:
          dataInputStream.readUnsignedShort();
          dataInputStream.readUnsignedShort();
          break;
        case CONSTANT_String:
          dataInputStream.readUnsignedShort();
          break;
        case CONSTANT_Integer:
        case CONSTANT_Float:
          dataInputStream.readInt();
          break;
        case CONSTANT_Long:
        case CONSTANT_Double:
          dataInputStream.readLong();
          idxPoolEntry++;
          break;
        case CONSTANT_NameAndType:
          dataInputStream.readUnsignedShort();
          dataInputStream.readUnsignedShort();
          break;
        case CONSTANT_Utf8:
          String fieldDescriptor = dataInputStream.readUTF();
          for (Map.Entry<String, Class<? extends Annotation>> entry : seekingAnnotationMap.entrySet()) {
            if (fieldDescriptor.equals(entry.getKey())) {
              annotatedClassname.put(seekingAnnotationMap.remove(entry.getKey()), classname);
            }
          }
        default:
          break;
      }
    }
  }

  private String getClassnameFrom(String path) {
    String classname = path;
    if (classname.contains(WEB_INF_CLASSES_FOLDER)) {
      classname = classname.substring(WEB_INF_CLASSES_FOLDER.length());
    }
    classname = classname.substring(0, classname.length() - CLASS_EXTENSION.length());
    return classname.replace('/', '.');
  }

  private Iterator<String> getsubPathIterator(String path) {
    return servletContext.getResourcePaths(path).iterator();
  }

}
