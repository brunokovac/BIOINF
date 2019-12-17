package hr.fer.bioinf.traversal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import hr.fer.bioinf.graph.Edge;
import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.graph.Node;

public class Approach3 implements Traversal {

	private static final int NUM_TRIALS = 1000;
	private static final int MAX_DEPTH = 700;

	private List<List<Node>> paths = new ArrayList<>();

	private Comparator<Edge> comparator = new Comparator<Edge>() {

		@Override
		public int compare(Edge e1, Edge e2) {
			double o1 = e1.getOverlapScore();
			double o2 = e2.getOverlapScore();

			if (o1 > o2) {
				return -1;
			}

			if (o1 < o2) {
				return 1;
			}

			// higher sequence identity or the longer read is selected
			double si1 = e1.getSequenceIdentity();
			double si2 = e2.getSequenceIdentity();

			if (si1 > si2) {
				return -1;
			}

			if (si1 < si2) {
				return 1;
			}

			return 0;
		}
	};

	private List<Node> path = new ArrayList<>();
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
					paths.add(new ArrayList<>(path));
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
						break;
					}
				}
			}
		}
	}

	private void reset() {
		path.clear();
		visited.clear();
	}

	@Override
	public List<List<Node>> findPaths(Graph graph) {
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
