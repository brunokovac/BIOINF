package hr.fer.bioinf.traversal;

import java.util.List;

import hr.fer.bioinf.graph.Graph;

public interface Traversal {
  List<TraversalPath> findPaths(Graph graph);
}
