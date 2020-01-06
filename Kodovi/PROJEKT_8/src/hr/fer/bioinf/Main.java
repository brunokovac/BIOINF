package hr.fer.bioinf;

import java.io.IOException;
import java.util.*;

import hr.fer.bioinf.graph.Edge;
import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.graph.Node;
import hr.fer.bioinf.traversal.*;
import hr.fer.bioinf.utils.Clock;

public class Main {

  public static void main(String[] args) throws IOException {
    Params.init(args);
    Clock clock = new Clock();

    // Build a graph
    clock.restart();
    Graph graph = Graph.loadFromFiles(Params.CONTIGS_PATH, Params.READS_PATH,
        Params.CONTIGS_READS_OVERLAPS_PATH, Params.READS_OVERLAPS_PATH);
    System.err.printf("[INFO] Graph successfully built (%d nodes, %d edges).  [%dms]%n",
        graph.getNodes().size(), graph.getEdges().size(), clock.elapsedTime());

    // Find paths between anchoring nodes
    clock.restart();
    List<TraversalPath> paths = new CombinedTraversal().findPaths(graph);
    System.err.printf("[INFO] Found %d paths between anchoring nodes.  [%dms]%n",
        paths.size(), clock.elapsedTime());

    paths = removeDuplicates(paths);
    paths.sort(Comparator.comparingInt(TraversalPath::getEstimatedLength));

    Map<String, List<TraversalPath>> pathsMapping = splitByID(paths);

    for (Map.Entry<String, List<TraversalPath>> ps : pathsMapping.entrySet()) {
      System.err.println();
      System.err.println("------ " + ps.getKey() + " ------- (" + ps.getValue().size() + ")");
      for (TraversalPath p : ps.getValue()) {
        System.err.println(p.id() + " " + p.getEstimatedLength() + " " + p.getEdges().size());
      }
    }

    debugPath(paths.get(500));
  }

  private static Map<String, List<TraversalPath>> splitByID(List<TraversalPath> paths) {
    Map<String, List<TraversalPath>> pathsMapping = new HashMap<>();
    for (TraversalPath path : paths) {
      if (!pathsMapping.containsKey(path.id())) {
        pathsMapping.put(path.id(), new ArrayList<>());
      }
      pathsMapping.get(path.id()).add(path);
    }
    return pathsMapping;
  }

  private static List<TraversalPath> removeDuplicates(List<TraversalPath> paths) {
    Set<Integer> hashes = new HashSet<>();
    List<TraversalPath> ret = new ArrayList<>();
    for (TraversalPath path : paths) {
      if (hashes.contains(path.hashCode())) continue;
      hashes.add(path.hashCode());
      ret.add(path);
    }
    return ret;
  }

  private static void debugPath(TraversalPath path) {
    for (Edge edge : path.getEdges()) {
      System.err.printf("[%9s => %9s] -- (from: %d %d) => (to: %d %d)  [OH: %d %d] [EX: %d %d]%n",
          edge.from().node().getID(), edge.to().node().getID(), edge.from().start(),
          edge.from().end(), edge.to().start(), edge.to().end(), edge.from().overhang(),
          edge.to().overhang(), edge.from().extension(), edge.to().extension());
    }
  }
}