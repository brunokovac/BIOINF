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

  @Option(
      name = "sequence-identity-cutoff",
      description = "Sequence identity cutoff",
      defaultValue = "0.7"
  )
  public static double SEQUENCE_IDENTITY_CUTOFF;

  @Option(
      name = "monte-carlo-iterations",
      description = "Monte Carlo number of trials to generate path",
      defaultValue = "15000"
  )
  public static int MONTE_CARLO_ITERATIONS;

  @Option(
      name = "consensus-window-size",
      description = "Consensus window size in bytes",
      defaultValue = "2000"
  )
  public static int CONSENSUS_WINDOW_SIZE;

  @Option(
      name = "contigs-path",
      description = "Contigs path",
      defaultValue = "ecoli_test_contigs.fasta"
  )
  public static String CONTIGS_PATH;

  @Option(
      name = "reads-path",
      description = "Reads path",
      defaultValue = "ecoli_test_reads.fasta"
  )
  public static String READS_PATH;

  @Option(
      name = "contigs-reads-overlaps-path",
      description = "Contigs-reads overlaps path",
      defaultValue = "reads_contigs_overlaps.paf"
  )
  public static String CONTIGS_READS_OVERLAPS_PATH;

  @Option(
      name = "reads-overlaps-path",
      description = "Reads-reads overlaps path",
      defaultValue = "reads_reads_overlaps.paf"
  )
  public static String READS_OVERLAPS_PATH;

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

    boolean help = false;
    for (String arg : args) {
      if (arg.equals("--help")) {
        help = true;
        continue;
      }
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

    if (help) {
      printOptions();
      System.exit(0);
    }
  }

  public static void printOptions() {
    System.out.println("Implementation of the HERA algorithm.");
    System.out.println("This project is a part of bioinformatics course at FER.");
    for (Field field : paramFields()) {
      Option annotation = field.getAnnotation(Option.class);
      System.out.println();
      System.out.println(annotation.name());
      System.out.println("    default: " + annotation.defaultValue());
      System.out.println("    " + annotation.description());
    }
  }
}