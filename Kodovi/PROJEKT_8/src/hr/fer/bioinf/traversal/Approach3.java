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
    for (Node node : graph.getNodes().values()) {
      List<EdgeEntry> edges = node.getRightNeighbours().entrySet().stream()
          .map(entry -> new EdgeEntry(entry.getKey(), entry.getValue()))
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
      edgeMap.put(node.getName(), edges);
    }
  }

  private EdgeEntry selectRandomEdge(Node node, Set<String> visited) {
    List<EdgeEntry> possibleEdges = edgeMap.get(node.getName()).stream()
        .filter(entry -> !visited.contains(entry.neighbor.getName()))
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
    List<Node> nodes = new ArrayList<>();
    List<Edge> edges = new ArrayList<>();
    nodes.add(startNode);
    Node currentNode = startNode;
    for (int i = 0; i < Params.MAX_DEPTH; ++i) {
      visited.add(currentNode.getName());
      EdgeEntry entry = selectRandomEdge(currentNode, visited);
      if (entry == null) {
        return null;
      }
      edges.add(entry.edge);
      nodes.add(entry.neighbor);
      currentNode = entry.neighbor;
      if (currentNode.isAnchor()) {
        return new TraversalPath(nodes, edges);
      }
    }
    return null;
  }

  @Override
  public List<TraversalPath> findPaths(Graph graph) {
    preprocessGraph(graph);

    List<TraversalPath> paths = new ArrayList<>();
    for (Node node : graph.getNodes().values()) {
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
