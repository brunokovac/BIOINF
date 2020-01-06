package hr.fer.bioinf.traversal;

import hr.fer.bioinf.Params;
import hr.fer.bioinf.graph.Edge;
import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.graph.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DFSTraversal implements Traversal {
  private List<TraversalPath> paths;
  private List<Edge> edges;
  private Set<String> visited;

  boolean DFS(Node node, int depth) {
    if (depth != 0 && node.isAnchor()) {
      paths.add(new TraversalPath(edges));
      return true;
    }
    if (depth >= Params.MAX_DEPTH) return false;

    visited.add(node.getID());

    List<Edge> neighbors = new ArrayList<>();
    for (Edge edge : node.getEdges()) {
      if (!visited.contains(edge.to().node().getID())) {
        neighbors.add(edge);
      }
    }
    // sort neighbors

    for (Edge edge : neighbors) {
      edges.add(edge);
      if (DFS(edge.to().node(), depth + 1)) {
        if (depth > 0) {
          edges.remove(edges.size() - 1);
          return true;
        }
      }
      edges.remove(edges.size() - 1);
    }

    return false;
  }

  @Override
  public List<TraversalPath> findPaths(Graph graph) {
    paths = new ArrayList<>();
    edges = new ArrayList<>();
    visited = new HashSet<>();
    for (Node node : graph.getNodes()) {
      if (!node.isAnchor()) continue;
      edges.clear();
      visited.clear();
      DFS(node, 0);
    }
    return paths;
  }
}
