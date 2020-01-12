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

public class Main {

  public static void main(String[] args) throws IOException {
    Params.init(args);
    Clock clock = new Clock();

    System.err.println(Params.EXCLUDED_CONTIGS.size());

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

    Map<String, List<TraversalPath>> pathsMapping = splitByID(paths);

    /*
    TraversalPath path12 = get(pathsMapping, "ctg0001[1]_ctg0002[0]");
    TraversalPath path23 = get(pathsMapping, "ctg0002[0]_ctg0003[1]");
    System.out.println(">output");
    System.out.println(path12.concat(path23).getSequence());
     */

    /*
    System.out.println(">output");
    TraversalPath p = get(pathsMapping, "ctg00001[0]_ctg00006[1]");
    System.out.println(p.getSequence());
    debugPath(p);*/

    /*
    List<TraversalPath> ppp = pathsMapping.get("ctg00003[0]_ctg00002[1]");
    for (int i = 0; i < ppp.size(); ++i) {
      if (i != 0) System.out.print(" ");
      System.out.print(ppp.get(i).getEstimatedLength());
    }
    System.out.println();
    */

    /*
        System.out.println(">lol1");
        System.out.println(p.getEdges().get(0).from().node().getData());
        System.out.println(">lol2");
        System.out.println(p.getEdges().get(p.getEdges().size() - 1).to().node().getData());
    */
    /*
    for (Map.Entry<String, List<TraversalPath>> ps : pathsMapping.entrySet()) {
      System.err.println();
      System.err.println("------ " + ps.getKey() + " ------- (" + ps.getValue().size() + ")");
      for (TraversalPath p : ps.getValue()) {
        System.err.println(p.id() + " " + p.getEstimatedLength() + " " + p.getEdges().size());
      }
    }
    */

    List<Consensus> consensuses =
        pathsMapping.values().stream().map(ConsensusBuilder::build).collect(Collectors.toList());
    consensuses.sort(Comparator.comparingInt(Consensus::getValidIndex));

    for (Consensus consensus : consensuses) {
      System.err.println(consensus.getPath().id() + " --> " + consensus.getValidIndex());
    }

    System.err.println();

    String prefix = "data/out/";
    List<TraversalPath> results = new SequenceBuilder(graph).build(consensuses);
    for (TraversalPath result : results) {
      System.err.println(result.id());
      debugPath(result);
      System.err.println();
    }
    for (int i = 0; i < results.size(); ++i) {
      TraversalPath path = results.get(i);
      BufferedWriter writer = new BufferedWriter(new FileWriter(prefix + path.id() + ".fasta"));
      writer.write(String.format(">%s%n", path.id()));
      writer.write(String.format("%s%n", path.getSequence()));
      writer.close();
    }
  }

  private static TraversalPath get(Map<String, List<TraversalPath>> paths, String id) {
    return paths.get(id).get(paths.get(id).size() / 3);
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
