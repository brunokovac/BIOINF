package hr.fer.bioinf.traversal;

import java.util.ArrayList;
import java.util.List;

import hr.fer.bioinf.graph.Edge;
import hr.fer.bioinf.graph.Node;
import hr.fer.bioinf.utils.Hasher;

/**
 * Model of traversal path. Path consists of series of edges from which the path of nodes is finally
 * generated.
 */
public class TraversalPath {
  private List<Edge> edges;

  public TraversalPath(List<Edge> edges) {
    this.edges = new ArrayList<>(edges);
  }

  public List<Edge> getEdges() {
    return edges;
  }

  /**
   * Concatenates other traversal path to the one on which the method is called.
   *
   * @param path other path
   * @return combined path
   */
  public TraversalPath concat(TraversalPath path) {
    List<Edge> combinedEdges = new ArrayList<>(edges);
    combinedEdges.addAll(path.edges);
    return new TraversalPath(combinedEdges);
  }


  /** Summary is a list of anchoring nodes in the sequence. */
  public String summary() {
    StringBuilder builder = new StringBuilder();
    for (Edge edge : edges) {
      Node node = edge.from().node();
      if (node.isAnchor()) {
        builder.append(node.summary());
        builder.append(',');
      }
    }
    Node last = edges.get(edges.size() - 1).to().node();
    builder.append(last.summary());
    return builder.toString();
  }

  public Node from() {
    return edges.get(0).from().node();
  }

  public Node to() {
    return edges.get(edges.size() - 1).to().node();
  }

  /**
   * Estimates path length.
   *
   * @return estimated length
   */
  public int getEstimatedLength() {
    int length = 0;
    int prev = 0;

    for (Edge edge : edges) {
      length += edge.from().end() - prev;
      prev = edge.to().end();
    }

    return length + edges.get(edges.size() - 1).to().node().length() - prev;
  }

  /**
   * Generates DNA sequence based on edges in path and corresponding nodes contained in the
   * traversal path.
   *
   * @return DNA sequence
   */
  public String getSequence() {
    StringBuilder builder = new StringBuilder();
    int prev = 0;
    for (Edge edge : edges) {
      int curr = Math.max(prev, edge.from().end());
      builder.append(edge.from().node().getData(), prev, curr);
      prev = edge.to().end();
    }
    builder.append(edges.get(edges.size() - 1).to().node().getData().substring(prev));
    return builder.toString();
  }

  @Override
  public int hashCode() {
    Hasher hasher = new Hasher();
    for (Edge edge : edges) {
      hasher.feed(edge.from().node().getID());
      hasher.feed(edge.from().node().isReversed());
      hasher.feed(edge.to().node().getID());
      hasher.feed(edge.to().node().isReversed());
    }
    return hasher.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (!(other instanceof TraversalPath)) return false;
    TraversalPath path = (TraversalPath) other;
    if (edges.size() != path.edges.size()) return false;
    for (int i = 0; i < edges.size(); ++i) {
      if (edges.get(i) != path.edges.get(i)) return false;
    }
    return true;
  }
}
