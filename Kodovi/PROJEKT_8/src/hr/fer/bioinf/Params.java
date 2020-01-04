package hr.fer.bioinf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class Params {
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  private @interface Option {
    String name();
    String description();
    String defaultValue();
  }

  @Option(
      name = "max-depth",
      description = "Max depth of DFS algorithm for path construction between two anchoring nodes",
      defaultValue = "500"
  )
  public static int MAX_DEPTH;

  private static List<Field> paramFields() {
    List<Field> fields = new ArrayList<>();
    for (Field field : Params.class.getDeclaredFields()) {
      if (!Modifier.isStatic(field.getModifiers()) || !field.isAnnotationPresent(Option.class))
        continue;
      fields.add(field);
    }
    return fields;
  }

  private static void setValue(Field field, String value) {
    try {
      if (field.getType().equals(int.class)) {
        field.set(null, Integer.parseInt(value));
      } else if (field.getType().equals(boolean.class)) {
        field.set(null, Boolean.parseBoolean(value));
      } else if (field.getType().equals(String.class)) {
        field.set(null, value);
      } else if (field.getType().equals(double.class)) {
        field.set(null, Double.parseDouble(value));
      }
    } catch (Exception e) {
      // TODO: ljepsa porukica
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void init(String[] args) {
    Map<String, Field> options = new HashMap<>();
    for (Field field : paramFields()) {
      Option annotation = field.getAnnotation(Option.class);
      setValue(field, annotation.defaultValue());
      options.put(annotation.name(), field);
    }

    for (String arg : args) {
      int equalSignPos = arg.indexOf('=');
      if (!arg.startsWith("--") || equalSignPos < 0 || equalSignPos >= arg.length()) {
        System.err.println("Please run ./executable --help.");
        System.exit(1);
      }
      String key = arg.substring(2, equalSignPos);
      String value = arg.substring(equalSignPos + 1);
      if (!options.keySet().contains(key)) {
        System.err.println("Unknown argument `" + key + "`.");
        System.err.println("Please run ./executable --help.");
        System.exit(1);
      }
      setValue(options.get(key), value);
    }
  }
}
