package hr.fer.bioinf.traversal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hr.fer.bioinf.graph.Edge;
import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.graph.Node;

public class DFSTraversal implements Traversal {

  private static final int MAX_DEPTH = 400;

  private List<TraversalPath> paths = new ArrayList<>();

  static Comparator<Edge> APPROACH_ONE =
      (e1, e2) -> {
        double o1 = e1.getOverlapScore();
        double o2 = e2.getOverlapScore();
        if (o1 != o2) return o1 > o2 ? 1 : -1;

        // higher sequence identity or the longer read is selected
        double si1 = e1.getSequenceIdentity();
        double si2 = e2.getSequenceIdentity();
        if (si1 != si2) return si1 > si2 ? 1 : -1;

        return 0;
      };

  static Comparator<Edge> APPROACH_TWO =
      (e1, e2) -> {
        double es1 = e1.getExtensionScore();
        double es2 = e2.getExtensionScore();
        if (es1 != es2) return es1 < es2 ? 1 : -1;
        return 0;
      };

  private List<Edge> edges = new ArrayList<>();
  private Set<String> visited = new HashSet<>();
  private boolean deadEnd = false;
  private Comparator<Edge> comparator;

  public DFSTraversal(Comparator<Edge> comparator) {
    this.comparator = comparator;
  }

  private void DFS(Node node, boolean right, int step) {
    if (node.isAnchor() && step > 0) {
      paths.add(new TraversalPath(edges));
      edges.remove(step - 1);
      return;
    }

    visited.add(node.getID());

    if (step == MAX_DEPTH - 1) {
      deadEnd = true;
      edges.remove(step - 1);
      return;
    }

    List<Edge> neighbours = node.getEdges();

    if (neighbours.isEmpty()) {
      deadEnd = true;
      if (!edges.isEmpty()) {
        edges.remove(step - 1);
      }
      return;
    }

    deadEnd = false;

    if (step == 0) {
      for (Edge neighbour : neighbours) {
        edges.add(neighbour);
        DFS(neighbour.to().node(), right, step + 1);
      }
    } else {

      List<Edge> possibleEdges = new ArrayList<>();
      for (Edge neighbour : neighbours) {
        if (!visited.contains(neighbour.to().node().getID())) {
          possibleEdges.add(neighbour);
        }
      }
      possibleEdges.sort(comparator);

      if (possibleEdges.isEmpty()) {
        deadEnd = true;
        edges.remove(step - 1);
        return;
      }

      edges.add(possibleEdges.get(0));
      DFS(possibleEdges.get(0).to().node(), right, step + 1);

      for (int i = 1; i < possibleEdges.size(); i++) {
        if (deadEnd) {
          edges.add(possibleEdges.get(i));
          DFS(possibleEdges.get(i).to().node(), right, step + 1);
        } else {
          break;
        }
      }

      edges.remove(step - 1);
    }
  }

  private void reset() {
    edges.clear();
    visited.clear();
    deadEnd = false;
  }

  @Override
  public List<TraversalPath> findPaths(Graph graph) {
    for (Node node : graph.getNodes()) {
      if (node.isAnchor()) {
        DFS(node, true, 0);
        reset();
      }
    }

    return paths;
  }
}
