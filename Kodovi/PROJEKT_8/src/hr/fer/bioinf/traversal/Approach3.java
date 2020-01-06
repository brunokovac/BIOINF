package hr.fer.bioinf.traversal;

import java.util.*;
import java.util.stream.Collectors;

import hr.fer.bioinf.Params;
import hr.fer.bioinf.graph.Edge;
import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.graph.Node;

public class Approach3 implements Traversal {
  private class EdgeEntry {
    Edge edge;
    Node neighbor;
    double accumulativeProbability;

    EdgeEntry(Edge edge, Node neighbor) {
      this.edge = edge;
      this.neighbor = neighbor;
      this.accumulativeProbability = 0.0;
    }
  }

  private Random random = new Random();
  private Map<String, List<EdgeEntry>> edgeMap;

  private void preprocessGraph(Graph graph) {
    edgeMap = new HashMap<>();
    for (Node node : graph.getNodes()) {
      List<EdgeEntry> edges = node.getEdges().stream()
          .map(edge -> new EdgeEntry(edge, edge.to().node()))
          .collect(Collectors.toList());
      double extensionScoreSum = 0;
      for (EdgeEntry entry : edges) {
        extensionScoreSum += entry.edge.getExtensionScore();
      }
      double accumulativeProbability = 0;
      for (EdgeEntry entry : edges) {
        accumulativeProbability += entry.edge.getSequenceIdentity() / extensionScoreSum;
        entry.accumulativeProbability = accumulativeProbability;
      }
      edgeMap.put(node.getID(), edges);
    }
  }

  private EdgeEntry selectRandomEdge(Node node, Set<String> visited) {
    List<EdgeEntry> possibleEdges = edgeMap.get(node.getID()).stream()
        .filter(entry -> !visited.contains(entry.neighbor.getID()))
        .collect(Collectors.toList());
    double extensionScoreSum = 0;
    for (EdgeEntry entry : possibleEdges) {
      extensionScoreSum += entry.edge.getExtensionScore();
    }
    double p = random.nextDouble() * extensionScoreSum;
    double c = 0;
    for (EdgeEntry entry : possibleEdges) {
      c += entry.edge.getExtensionScore();
      if (c >= p) {
        return entry;
      }
    }
    return null;
  }

  private TraversalPath randomPath(Node startNode) {
    Set<String> visited = new HashSet<>();
    List<Edge> edges = new ArrayList<>();
    Node currentNode = startNode;
    for (int i = 0; i < Params.MAX_DEPTH; ++i) {
      visited.add(currentNode.getID());
      EdgeEntry entry = selectRandomEdge(currentNode, visited);
      if (entry == null) {
        return null;
      }
      edges.add(entry.edge);
      currentNode = entry.neighbor;
      if (currentNode.isAnchor()) {
        return new TraversalPath(edges);
      }
    }
    return null;
  }

  @Override
  public List<TraversalPath> findPaths(Graph graph) {
    preprocessGraph(graph);

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
