package hr.fer.bioinf.traversal;

import java.util.*;
import java.util.stream.Collectors;

import hr.fer.bioinf.Params;
import hr.fer.bioinf.graph.Edge;
import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.graph.Node;

/**
 * Graph traversal based on Monte Carlo approach where each next node is selected based on its
 * extension score (bigger extension score equals proportionally better chance to get selected for
 * next step). This approach is specified by number of trials and max depth.
 */
public class MonteCarloTraversal implements Traversal {
  private Random random = new Random();

  /**
   * Selects random edge for expanding to a new node.
   *
   * @param node current node
   * @param visited set of visited nodes
   * @return selected edge
   */
  private Edge selectRandomEdge(Node node, Set<String> visited) {
    List<Edge> possibleEdges =
        node.getEdges().stream()
            .filter(edge -> !visited.contains(edge.to().node().getID()))
            .collect(Collectors.toList());
    double extensionScoreSum = 0;
    for (Edge edge : possibleEdges) {
      extensionScoreSum += edge.getExtensionScore();
    }
    double p = random.nextDouble() * extensionScoreSum;
    double c = 0;
    for (Edge edge : possibleEdges) {
      c += edge.getExtensionScore();
      if (c >= p) {
        return edge;
      }
    }
    return null;
  }

  /**
   * Generates random traversal path from given starting node.
   *
   * @param startNode starting node
   * @return generated path
   */
  private TraversalPath randomPath(Node startNode) {
    Set<String> visited = new HashSet<>();
    List<Edge> edges = new ArrayList<>();
    Node currentNode = startNode;
    for (int i = 0; i < Params.MAX_DEPTH; ++i) {
      visited.add(currentNode.getID());
      Edge edge = selectRandomEdge(currentNode, visited);
      if (edge == null) {
        return null;
      }
      edges.add(edge);
      currentNode = edge.to().node();
      if (currentNode.isAnchor()) {
        return new TraversalPath(edges);
      }
    }
    return null;
  }

  @Override
  public List<TraversalPath> findPaths(Graph graph) {
    List<TraversalPath> paths = new ArrayList<>();
    for (Node node : graph.getNodes()) {
      if (!node.isAnchor()) {
        continue;
      }
      for (int iter = 0; iter < Params.MONTE_CARLO_ITERATIONS; ++iter) {
        TraversalPath path = randomPath(node);
        if (path != null) {
          paths.add(path);
        }
      }
    }

    return paths;
  }
}
