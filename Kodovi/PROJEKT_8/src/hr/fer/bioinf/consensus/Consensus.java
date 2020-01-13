package hr.fer.bioinf.consensus;

import hr.fer.bioinf.traversal.TraversalPath;

public class Consensus {
  private TraversalPath path;
  private int validIndex;

  Consensus(TraversalPath path, int validIndex) {
    this.path = path;
    this.validIndex = validIndex;
  }

  public TraversalPath getPath() {
    return path;
  }

  public int getValidIndex() {
    return validIndex;
  }
}
