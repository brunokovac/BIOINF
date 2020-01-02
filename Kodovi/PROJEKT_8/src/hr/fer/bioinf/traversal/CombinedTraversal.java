package hr.fer.bioinf.traversal;

import hr.fer.bioinf.graph.Graph;

import java.util.ArrayList;
import java.util.List;

public class CombinedTraversal implements Traversal {
  public List<TraversalPath> findPaths(Graph graph) {
    List<TraversalPath> paths = new ArrayList<>();
    Traversal[] approaches = {new Approach3()};
    for (Traversal traversal : approaches) {
      paths.addAll(traversal.findPaths(graph));
    }
    return paths;
  }
}
