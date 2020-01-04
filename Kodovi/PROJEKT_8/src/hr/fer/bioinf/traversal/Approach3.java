package hr.fer.bioinf.traversal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import hr.fer.bioinf.graph.Edge;
import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.graph.Node;

public class Approach3 implements Traversal {

	private static final int NUM_TRIALS = 1500;
	private static final int MAX_DEPTH = 600;

	private List<TraversalPath> paths = new ArrayList<>();

	private List<Node> path = new ArrayList<>();
	private List<Edge> edges = new ArrayList<>();
	private Set<String> visited = new HashSet<>();
	private Random random = new Random();

	private void MonteCarlo(Node node, boolean right) {
		for (int k = 0; k < NUM_TRIALS; k++) {
			Node currentNode = node;
			reset();

			for (int i = 0; i < MAX_DEPTH; i++) {
				path.add(currentNode);
				visited.add(currentNode.getName());

				if (i > 0 && currentNode.isAnchor()) {
					paths.add(new TraversalPath(path, edges));
					break;
				}

				Map<Edge, Node> neighbours = right ? currentNode.getRightNeighbours() : currentNode.getLeftNeighbours();

				List<Edge> possibleEdges = new ArrayList<>();
				for (Map.Entry<Edge, Node> neighbour : neighbours.entrySet()) {
					if (!visited.contains(neighbour.getValue().getName())) {
						possibleEdges.add(neighbour.getKey());
					}
				}

				if (possibleEdges.size() == 0) {
					break;
				}

				double totalSum = 0.0;
				for (Edge e : possibleEdges) {
					totalSum += e.getExtensionScore();
				}

				double value = random.nextDouble() * totalSum;
				double currentSum = 0.0;
				for (Edge e : possibleEdges) {
					currentSum += e.getExtensionScore();
					if (currentSum > value) {
						currentNode = neighbours.get(e);
						edges.add(e);
						break;
					}
				}
			}
		}
	}

	private void reset() {
		path.clear();
		edges.clear();
		visited.clear();
	}

	@Override
	public List<TraversalPath> findPaths(Graph graph) {
		for (Node node : graph.getNodes().values()) {
			if (node.isAnchor()) {
				MonteCarlo(node, true);
				// DFS(node, false, 1, true);
				// reset();
			}
		}

		return paths;
	}

}
