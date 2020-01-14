package hr.fer.bioinf.traversal;

import hr.fer.bioinf.graph.Graph;

import java.util.ArrayList;
import java.util.List;

/** Class implementing a graph traversal combined of all other specific traversals. */
public class CombinedTraversal implements Traversal {
  /** Finds all paths in graph using all traversal approaches. */
  public List<TraversalPath> findPaths(Graph graph) {
    List<TraversalPath> paths = new ArrayList<>();
    Traversal[] approaches = {
      new DFSTraversal(DFSTraversal.APPROACH_ONE),
      new DFSTraversal(DFSTraversal.APPROACH_TWO),
      new MonteCarloTraversal()
    };
    for (Traversal traversal : approaches) {
      paths.addAll(traversal.findPaths(graph));
    }
    return paths;
  }
}
