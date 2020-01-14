package hr.fer.bioinf.graph;

/** Class modeling a directed edge between two nodes in a graph. */
public class Edge {
  /**
   * Class containing data about nodes that the edge connects. The data is useful for calculating
   * various statistics on edge.
   */
  public static class NodeData {
    Node node;
    int start;
    int end;
    int overhang;
    int extension;

    NodeData(Node node, int start, int end, int overhang, int extension) {
      this.node = node;
      this.start = start;
      this.end = end;
      this.overhang = overhang;
      this.extension = extension;

      // Ensure that partial segments sum up to entire sequence.
      if (end - start + overhang + extension != node.length()) {
        System.err.println("[ERROR]: Edge.NodeData::ctor() segments don't sum up.");
        System.err.printf(
            "          (%d %d)  O: %d  E: %d,  %d%n",
            start, end, overhang, extension, node.length());
        System.exit(1);
      }
    }

    public Node node() {
      return node;
    }

    public int end() {
      return end;
    }

    public int start() {
      return start;
    }

    public int overhang() {
      return overhang;
    }

    public int extension() {
      return extension;
    }
  }

  /** Data of the starting node. */
  private NodeData from;

  /** Data of the ending node. */
  private NodeData to;

  private int numberOfResidueMatches;
  private int alignmentBlockLength;

  private double sequenceIdentity;
  private double overlapScore;
  private double extensionScore;

  Edge(NodeData from, NodeData to, int numberOfResidueMatches, int alignmentBlockLength) {
    this.from = from;
    this.to = to;
    this.numberOfResidueMatches = numberOfResidueMatches;
    this.alignmentBlockLength = alignmentBlockLength;

    initScores();
  }

  public NodeData from() {
    return this.from;
  }

  public NodeData to() {
    return this.to;
  }

  /**
   * A helper method that initializes sequence identity, overlap score and extension score based on
   * overlap data from two reads/contigs.
   *
   * <p>It is called once before using those statistics.
   */
  private void initScores() {
    int fromOverlap = from.end - from.start;
    int toOverlap = to.end - to.start;
    sequenceIdentity = (double) numberOfResidueMatches / Math.max(fromOverlap, toOverlap);
    overlapScore = (fromOverlap + toOverlap) * sequenceIdentity / 2;
    extensionScore = overlapScore + to.extension / 2.0 - (from.overhang + to.overhang) / 2.0;
  }

  public double getSequenceIdentity() {
    return sequenceIdentity;
  }

  public double getOverlapScore() {
    return overlapScore;
  }

  public double getExtensionScore() {
    return extensionScore;
  }
}
