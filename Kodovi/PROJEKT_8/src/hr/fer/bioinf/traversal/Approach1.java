package hr.fer.bioinf.traversal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import hr.fer.bioinf.graph.Edge;
import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.graph.Node;

public class Approach1 implements Traversal {

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

			return 1;
		}
	};

	private List<Node> path = new ArrayList<>();
	private boolean deadEnd = true;

	private void DFS(Node node, boolean right, int step) {
		path.add(node);

		if (node.isAnchor() && step != 1) {
			paths.add(new ArrayList<>(path));
			path.remove(path.size() - 1);
			return;
		}

		Map<Edge, Node> neighbours = right ? node.getRightNeighbours() : node.getLeftNeighbours();

		if (step == 1) {
			for (Node neighbour : neighbours.values()) {
				DFS(neighbour, right, step + 1);
			}
		} else {
			if (neighbours.isEmpty()) {
				deadEnd = true;
			} else {
				List<Edge> edges = new ArrayList<>(neighbours.keySet());
				Collections.sort(edges, comparator);

				for (Edge edge : edges) {
					if (deadEnd) {
						deadEnd = false;
						DFS(neighbours.get(edge), right, step + 1);
					}
				}
			}
		}
	}

	@Override
	public List<List<Node>> findPaths(Graph graph) {
		for (Node node : graph.getNodes().values()) {
			if (node.isAnchor()) {
				DFS(node, true, 1);
				path.clear();
				DFS(node, false, 1);
				path.clear();
			}
		}

		return paths;
	}

}
