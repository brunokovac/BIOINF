package hr.fer.bioinf.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generator tool for creating artifical test data for evaluating the algorithm.
 */
public class DataGenerator {
  public static void main(String[] args) {
    // Read arguments...
    String outputPrefix = args[0];
    int referenceLength = Integer.parseInt(args[1]);
    int numContigs = readIntOr(args, 2, 3);
    int numReads = readIntOr(args, 3, 200);
    int readLength = readIntOr(args, 4, 5 * referenceLength / numReads);
    int contigGaps = readIntOr(args, 5, 5 * referenceLength / numReads);
    boolean allowNegativeStrand = readBooleanOr(args, 6, false);
    double errorRate = readDoubleOr(args, 7, 0.0);

    // Generate and save reference
    String reference = generateReference(referenceLength);
    saveFasta(Collections.singletonList(reference), "reference", outputPrefix + "_reference.fasta");

    // Generate contigs
    int contigOffset = referenceLength / numContigs;
    int contigLength = contigOffset - contigGaps;
    List<String> contigs = new ArrayList<>();
    for (int i = 0; i < numContigs; ++i) {
      int start = i * contigOffset;
      int end = start + contigLength;
      contigs.add(reference.substring(start, end));
    }
    contigs = applyModifications(contigs, allowNegativeStrand, errorRate);
    saveFasta(contigs, "ctg", outputPrefix + "_contigs.fasta");

    // Generate reads
    int readOffset = (referenceLength - readLength) / numReads;
    List<String> reads = new ArrayList<>();
    for (int i = 0; i < numReads; ++i) {
      int start = i * readOffset;
      int end = start + readLength;
      reads.add(reference.substring(start, end));
    }
    reads = applyModifications(reads, allowNegativeStrand, errorRate);
    saveFasta(reads, "read", outputPrefix + "_reads.fasta");
  }

  private static void saveFasta(List<String> sequences, String naming, String outputPath) {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, false));
      for (int i = 0; i < sequences.size(); ++i) {
        writer.write(String.format(">%s%04d%n", naming, i + 1));
        writer.write(String.format("%s%n", sequences.get(i)));
      }
      writer.close();
    } catch (IOException e) {
      System.err.println("Unable to open " + outputPath + " for writing.");
      System.exit(1);
    }
  }

  private static List<String> applyModifications(
      List<String> sequences, boolean allowNegative, double errorRate) {
    Random random = new Random();
    return sequences.stream()
        .map(
            seq -> {
              if (allowNegative && random.nextBoolean()) seq = revertedStrand(seq);
              seq = randomNoise(seq, errorRate);
              return seq;
            })
        .collect(Collectors.toList());
  }

  private static String revertedStrand(String sequence) {
    StringBuilder builder = new StringBuilder();
    for (int i = sequence.length() - 1; i >= 0; --i) {
      char c = sequence.charAt(i);
      if (c == 'A') builder.append('T');
      if (c == 'C') builder.append('G');
      if (c == 'G') builder.append('C');
      if (c == 'T') builder.append('A');
    }
    return builder.toString();
  }

  private static String randomNoise(String sequence, double errorRate) {
    char[] options = new char[] {'A', 'C', 'G', 'T'};
    Random random = new Random();
    StringBuilder builder = new StringBuilder();
    int skipAppend = 0;
    for (char i = 0; i < sequence.length(); ++i) {
      if (random.nextDouble() < errorRate) {
        int k = random.nextInt(3);
        if (k == 0) {
          // erase
          skipAppend--;
        } else if (k == 1) {
          // modify
          skipAppend--;
          builder.append(options[random.nextInt(4)]);
        } else {
          // insert
          builder.append(options[random.nextInt(4)]);
        }
      }
      if (skipAppend == 0) {
        builder.append(sequence.charAt(i));
      } else {
        skipAppend++;
      }
    }
    return builder.toString();
  }

  /** A helper method that generates random sequences of characters A, C, G, T. */
  private static String generateReference(int length) {
    char[] options = new char[] {'A', 'C', 'G', 'T'};
    Random random = new Random();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length; ++i) {
      builder.append(options[random.nextInt(4)]);
    }
    return builder.toString();
  }

  private static int readIntOr(String[] args, int index, int defaultValue) {
    if (index >= args.length) return defaultValue;
    return Integer.parseInt(args[index]);
  }

  private static double readDoubleOr(String[] args, int index, double defaultValue) {
    if (index >= args.length) return defaultValue;
    return Double.parseDouble(args[index]);
  }

  private static boolean readBooleanOr(String[] args, int index, boolean defaultValue) {
    if (index >= args.length) return defaultValue;
    return Boolean.parseBoolean(args[index]);
  }
}
