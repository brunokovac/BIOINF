package hr.fer.bioinf.graph;

import hr.fer.bioinf.Params;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Class modeling a graph constructed based on overlaps between reads and contigs. Overlaps are
 * acquired using minimap tool and loaded through .paf files.
 */
public class Graph {
  /** Model of node containing both original read and reversed variants of the sequence. */
  public static class NodeDualPair {
    private Node original;
    private Node reversed;

    NodeDualPair(String id, String data, boolean anchor) {
      original = new Node(id, data, false, anchor);
      StringBuilder reversedDataBuilder = new StringBuilder();
      for (int i = data.length() - 1; i >= 0; --i) {
        char c = data.charAt(i);
        char append = '?';
        if (c == 'A') append = 'T';
        if (c == 'C') append = 'G';
        if (c == 'G') append = 'C';
        if (c == 'T') append = 'A';
        reversedDataBuilder.append(append);
      }
      reversed = new Node(id, reversedDataBuilder.toString(), true, anchor);
    }

    public Node original() {
      return original;
    }

    public Node reversed() {
      return reversed;
    }
  }

  /**
   * Class containing one half of the overlap information between two nodes acquired by minimap,
   * such as starting and ending positions of the overlap on one node (query or target).
   */
  private static class HalfEdge {
    Node node;
    int start;
    int end;
    int sequenceLength;

    HalfEdge(Node node, int start, int end, int sequenceLength) {
      this.node = node;
      this.start = start;
      this.end = end;
      this.sequenceLength = sequenceLength;
    }
  }

  private Map<String, NodeDualPair> nodes;
  private List<Edge> edges;
  private List<Node> allNodes;

  Graph() {
    nodes = new HashMap<>();
    edges = new ArrayList<>();
    allNodes = new ArrayList<>();
  }

  /**
   * Adds nodes to graph, both original and reversed.
   *
   * @param nodeDualPair node pair
   */
  private void addNode(NodeDualPair nodeDualPair) {
    // assert nodeDualPair.original.id == nodeDualPair.reversed.id
    nodes.put(nodeDualPair.original().getID(), nodeDualPair);
    allNodes.add(nodeDualPair.original());
    allNodes.add(nodeDualPair.reversed());
  }

  public NodeDualPair getNodePair(String id) {
    return nodes.get(id);
  }

  public Collection<Node> getNodes() {
    return this.allNodes;
  }

  public Node getNode(String id, boolean reversed) {
    NodeDualPair nodeDualPair = nodes.get(id);
    if (nodeDualPair == null) return null;
    if (reversed) return nodeDualPair.reversed();
    return nodeDualPair.original();
  }

  /**
   * Adds edge to graph. If its sequence identity or extension score is less than 0, edge is not
   * added.
   *
   * @param edge
   */
  private void addEdge(Edge edge) {
    if (edge.getSequenceIdentity() < Params.SEQUENCE_IDENTITY_CUTOFF
        || edge.getExtensionScore() < 0) {
      return;
    }
    edge.from().node.addEdge(edge);
    edges.add(edge);
  }

  public List<Edge> getEdges() {
    return edges;
  }

  /**
   * Constructs graph instance from specific-form files.
   *
   * @param contigsPath path to file containing contigs data
   * @param readsPath path to file containing reads data
   * @param contigsReadsOverlapsPath path to contig-read overlaps
   * @param readsReadsOverlapsPath path to file containing read-read overlaps
   * @return graph instance
   * @throws IOException
   */
  public static Graph loadFromFiles(
      String contigsPath,
      String readsPath,
      String contigsReadsOverlapsPath,
      String readsReadsOverlapsPath)
      throws IOException {
    Graph graph = new Graph();

    parseFastaFile(contigsPath, graph, true);
    parseFastaFile(readsPath, graph, false);

    parsePafFile(contigsReadsOverlapsPath, graph);
    parsePafFile(readsReadsOverlapsPath, graph);

    return graph;
  }

  /**
   * Parses fasta file containing content for each read or contig. Adds reads and contigs to graph.
   *
   * @param path path to fasta file
   * @param graph graph instance
   * @param anchor flag marking if nodes are anchors or not
   * @throws IOException
   */
  private static void parseFastaFile(String path, Graph graph, boolean anchor) throws IOException {
    List<String> lines = Files.readAllLines(Paths.get(path));
    for (int i = 0, size = lines.size(); i < size; i += 2) {
      String name = lines.get(i).trim().substring(1);
      if (Params.EXCLUDED_CONTIGS.contains(name)) {
        continue;
      }
      String data = lines.get(i + 1).trim();
      graph.addNode(new NodeDualPair(name, data, anchor));
    }
  }

  /**
   * Parses paf file describing overlaps between nodes. Adds edges between graph nodes.
   *
   * @param path path to paf file
   * @param graph graph instance
   * @throws IOException
   */
  private static void parsePafFile(String path, Graph graph) throws IOException {
    for (String line : Files.readAllLines(Paths.get(path))) {
      String data[] = line.split("\t");

      String querySequenceName = data[0];
      int querySequenceLength = Integer.parseInt(data[1]);
      int queryStart = Integer.parseInt(data[2]); // closed
      int queryEnd = Integer.parseInt(data[3]); // open
      char relativeStrand = data[4].charAt(0);
      String targetSequenceName = data[5];
      int targetSequenceLength = Integer.parseInt(data[6]);
      int targetStart = Integer.parseInt(data[7]); // on original strand
      int targetEnd = Integer.parseInt(data[8]); // on original strand
      int numberOfResidueMatches = Integer.parseInt(data[9]);
      int alignmentBlockLength = Integer.parseInt(data[10]);

      if (Params.EXCLUDED_CONTIGS.contains(querySequenceName)
          || Params.EXCLUDED_CONTIGS.contains(targetSequenceName)) {
        continue;
      }

      // za dualnost:
      int queryStartInv = querySequenceLength - queryEnd;
      int queryEndInv = querySequenceLength - queryStart;
      int targetStartInv = targetSequenceLength - targetEnd;
      int targetEndInv = targetSequenceLength - targetStart;

      if (querySequenceName.equals(targetSequenceName)) {
        continue;
      }

      NodeDualPair queryNodePair = graph.getNodePair(querySequenceName);
      NodeDualPair targetNodePair = graph.getNodePair(targetSequenceName);

      HalfEdge[] queryHalves = {
        new HalfEdge(queryNodePair.original(), queryStart, queryEnd, querySequenceLength),
        new HalfEdge(queryNodePair.reversed(), queryStartInv, queryEndInv, querySequenceLength)
      };

      HalfEdge[] targetHalves = {
        new HalfEdge(targetNodePair.original(), targetStart, targetEnd, targetSequenceLength),
        new HalfEdge(targetNodePair.reversed(), targetStartInv, targetEndInv, targetSequenceLength)
      };

      for (HalfEdge query : queryHalves) {
        for (HalfEdge target : targetHalves) {
          char strand = (query.node.isReversed() == target.node.isReversed() ? '+' : '-');
          if (strand == relativeStrand) {
            mergeHalves(graph, query, target, numberOfResidueMatches, alignmentBlockLength);
          }
        }
      }
    }
  }

  /**
   * A helper method that tries to merge two halves (query and target half) and add edge to graph.
   */
  private static void mergeHalves(
      Graph graph,
      HalfEdge query,
      HalfEdge target,
      int numberOfResidueMatches,
      int alignmentBlockLength) {
    // >>qqqq[qq]q
    //      t[tt]tttttt>>
    if (query.start > target.start
        && (query.sequenceLength - query.end) < (target.sequenceLength - target.end)) {
      Edge.NodeData from =
          new Edge.NodeData(
              query.node, query.start, query.end, query.sequenceLength - query.end, query.start);
      Edge.NodeData to =
          new Edge.NodeData(
              target.node,
              target.start,
              target.end,
              target.start,
              target.sequenceLength - target.end);
      graph.addEdge(new Edge(from, to, numberOfResidueMatches, alignmentBlockLength));
    }

    //     q[qqq]qqqqq>>
    // >>ttt[ttt]t
    if (query.start < target.start
        && (query.sequenceLength - query.end) > (target.sequenceLength - target.end)) {
      Edge.NodeData from =
          new Edge.NodeData(
              target.node,
              target.start,
              target.end,
              target.sequenceLength - target.end,
              target.start);
      Edge.NodeData to =
          new Edge.NodeData(
              query.node, query.start, query.end, query.start, query.sequenceLength - query.end);
      graph.addEdge(new Edge(from, to, numberOfResidueMatches, alignmentBlockLength));
    }
  }
}
