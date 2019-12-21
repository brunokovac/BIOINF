package hr.fer.bioinf.traversal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hr.fer.bioinf.graph.Edge;
import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.graph.Node;

public class Approach2 implements Traversal {

	private static final int MAX_DEPTH = 250;

	private List<TraversalPath> paths = new ArrayList<>();

	private Comparator<Edge> comparator = new Comparator<Edge>() {

		@Override
		public int compare(Edge e1, Edge e2) {
			double o1 = e1.getExtensionScore();
			double o2 = e2.getExtensionScore();

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
	private List<Edge> edges = new ArrayList<>();
	private Set<String> visited = new HashSet<>();
	private boolean deadEnd = false;

	private void DFS(Node node, boolean right, int step) {
		if (node.isAnchor() && step > 0) {
			path.add(node);
			paths.add(new TraversalPath(path, edges));
			path.remove(step);
			edges.remove(step - 1);
			return;
		}

		visited.add(node.getName());

		if (step == MAX_DEPTH - 1) {
			deadEnd = true;
			edges.remove(step - 1);
			return;
		}

		Map<Edge, Node> neighbours = right ? node.getRightNeighbours() : node.getLeftNeighbours();

		if (neighbours.isEmpty()) {
			deadEnd = true;
			if (!edges.isEmpty()) {
				edges.remove(step - 1);
			}
			return;
		}

		path.add(node);
		deadEnd = false;

		if (step == 0) {
			for (Map.Entry<Edge, Node> neighbour : neighbours.entrySet()) {
				edges.add(neighbour.getKey());
				DFS(neighbour.getValue(), right, step + 1);
			}
		} else {

			List<Edge> possibleEdges = new ArrayList<>();
			for (Map.Entry<Edge, Node> neighbour : neighbours.entrySet()) {
				if (!visited.contains(neighbour.getValue().getName())) {
					possibleEdges.add(neighbour.getKey());
				}
			}
			possibleEdges.sort(comparator);

			if (possibleEdges.isEmpty()) {
				deadEnd = true;
				path.remove(step);
				edges.remove(step - 1);
				return;
			}

			edges.add(possibleEdges.get(0));
			DFS(neighbours.get(possibleEdges.get(0)), right, step + 1);

			for (int i = 1; i < possibleEdges.size(); i++) {
				if (deadEnd) {
					edges.add(possibleEdges.get(i));
					DFS(neighbours.get(possibleEdges.get(i)), right, step + 1);
				} else {
					break;
				}
			}

			path.remove(step);
			edges.remove(step - 1);

		}
	}

	private void reset() {
		path.clear();
		edges.clear();
		visited.clear();
		deadEnd = false;
	}

	@Override
	public List<TraversalPath> findPaths(Graph graph) {
		for (Node node : graph.getNodes().values()) {
			if (node.isAnchor()) {
				DFS(node, true, 0);
				reset();
			}
		}

		return paths;
	}

}
