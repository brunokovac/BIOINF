package hr.fer.bioinf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import hr.fer.bioinf.consensus.Consensus;
import hr.fer.bioinf.consensus.ConsensusBuilder;
import hr.fer.bioinf.construction.SequenceBuilder;
import hr.fer.bioinf.graph.Edge;
import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.traversal.*;
import hr.fer.bioinf.utils.Clock;

/**
 * Class containing methods for starting the whole algorithm.
 */
public class Main {

  /**
   * Main method for starting the algorithm. Takes input parameters (reads, overlaps, SI cutoff,
   * max depth for DFS etc.) and generates final DNA sequences.
   *
   * @param args configuration parameters
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    System.out.println(ProcessHandle.current().pid());

    Params.init(args);
    Clock clock = new Clock();

    // Build a graph
    clock.restart();
    Graph graph =
        Graph.loadFromFiles(
            Params.CONTIGS_PATH,
            Params.READS_PATH,
            Params.CONTIGS_READS_OVERLAPS_PATH,
            Params.READS_OVERLAPS_PATH);
    System.err.printf(
        "[INFO] Graph successfully built (%d nodes, %d edges).  [%dms]%n",
        graph.getNodes().size(), graph.getEdges().size(), clock.elapsedTime());

    // Find paths between anchoring nodes
    clock.restart();
    List<TraversalPath> paths = new CombinedTraversal().findPaths(graph);
    int pathsSizeStart = paths.size();
    paths = removeDuplicates(paths);
    int pathsSizeEnd = paths.size();
    paths.sort(Comparator.comparingInt(TraversalPath::getEstimatedLength));
    System.err.printf(
        "[INFO] Found %d paths between anchoring nodes (removed %d duplicates).  [%dms]%n",
        paths.size(), pathsSizeStart - pathsSizeEnd, clock.elapsedTime());

    // Find consensuses and construct output sequences
    clock.restart();
    Map<String, List<TraversalPath>> pathsMapping = splitByID(paths);
    List<Consensus> consensuses =
        pathsMapping.values().stream()
            .map(ConsensusBuilder::build)
            .sorted(Comparator.comparingInt(Consensus::getValidIndex))
            .collect(Collectors.toList());
    List<TraversalPath> results = new SequenceBuilder(graph).buildUsingConflictIndex(consensuses);
    System.err.printf(
        "[INFO] Found %d resulting paths.  [%dms]%n", results.size(), clock.elapsedTime());

    // Output paths
    for (TraversalPath path : results) {
      BufferedWriter writer =
          new BufferedWriter(new FileWriter(Params.OUTPUT_FOLDER + path.summary() + ".fasta"));
      writer.write(String.format(">%s%n", path.summary()));
      writer.write(String.format("%s%n", path.getSequence()));
      writer.close();

      System.err.println();
      System.err.println(path.summary());
      debugPath(path);
    }
  }

  /**
   * Splits all paths into categories by their id (defined by starting and ending contigs).
   *
   * @param paths all paths
   * @return Map of contigs split by their starting and ending contigs.
   */
  private static Map<String, List<TraversalPath>> splitByID(List<TraversalPath> paths) {
    Map<String, List<TraversalPath>> pathsMapping = new HashMap<>();
    for (TraversalPath path : paths) {
      if (!pathsMapping.containsKey(path.summary())) {
        pathsMapping.put(path.summary(), new ArrayList<>());
      }
      pathsMapping.get(path.summary()).add(path);
    }
    return pathsMapping;
  }

  /**
   * Removes duplicate paths.
   *
   * @param paths all paths
   */
  private static List<TraversalPath> removeDuplicates(List<TraversalPath> paths) {
    return new ArrayList<>(new HashSet<>(paths));
  }

  private static void debugPath(TraversalPath path) {
    for (Edge edge : path.getEdges()) {
      System.err.printf(
          "[%9s => %9s] -- (from: %d %d) => (to: %d %d)  [OH: %d %d] [EX: %d %d]%n",
          edge.from().node().getID(),
          edge.to().node().getID(),
          edge.from().start(),
          edge.from().end(),
          edge.to().start(),
          edge.to().end(),
          edge.from().overhang(),
          edge.to().overhang(),
          edge.from().extension(),
          edge.to().extension());
    }
  }
}
