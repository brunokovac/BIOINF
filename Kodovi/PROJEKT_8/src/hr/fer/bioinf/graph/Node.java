package hr.fer.bioinf.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Class modeling each graph node. Node consists of its id, read content, reversed flag based on
 * reading strand and information if the node is anchoring node. Each node also contains information
 * about outgoing edges that connect it to other nodes.
 */
public class Node {
  /** Refers to the id of its read or contig. */
  private String id;

  private String data;
  private boolean reversed;
  private boolean anchor;

  private List<Edge> edges;

  Node(String id, String data, boolean reversed, boolean anchor) {
    this.id = id;
    this.data = data;
    this.reversed = reversed;
    this.anchor = anchor;
    this.edges = new ArrayList<>();
  }

  public String getID() {
    return id;
  }

  public String summary() {
    char strand = reversed ? '-' : '+';
    return String.format("%s[%c]", id, strand);
  }

  public String getData() {
    return data;
  }

  public boolean isReversed() {
    return reversed;
  }

  public boolean isAnchor() {
    return anchor;
  }

  public int length() {
    return data.length();
  }

  public void addEdge(Edge edge) {
    // Ensure that starting node of the edge is this node.
    if (edge.from().node != this) {
      System.err.println("[ERROR] Node::addEdge() received unexpected starting node.");
    }
    edges.add(edge);
  }

  public List<Edge> getEdges() {
    return edges;
  }

  @Override
  public int hashCode() {
    // Node is uniquely defined by tis id and reverse flag.
    int ret = id.hashCode() * 2;
    if (reversed) ret++;
    return ret;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Node)) return false;
    Node node = (Node) other;
    // Node is uniquely defined by tis id and reverse flag.
    return id.equals(node.id) && reversed == node.reversed;
  }
}
