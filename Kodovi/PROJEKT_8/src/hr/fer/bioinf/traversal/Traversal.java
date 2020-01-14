package hr.fer.bioinf.traversal;

import java.util.List;

import hr.fer.bioinf.graph.Graph;

/** Interface specifying generic method for finding graph paths between anchoring nodes. */
public interface Traversal {
  /**
   * Finds paths between each pair of anchoring nodes.
   *
   * @param graph graph instance
   * @return found paths
   */
  List<TraversalPath> findPaths(Graph graph);
}
