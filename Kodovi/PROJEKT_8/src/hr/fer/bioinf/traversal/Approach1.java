package hr.fer.bioinf.traversal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hr.fer.bioinf.graph.Edge;
import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.graph.Node;

public class Approach1 implements Traversal {

	private Set<List<Node>> paths = new HashSet<>();

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

	private void DFS(Node node, int step, int highestScoreIndex, boolean right) {
		if (node.isAnchor() && step != 1) {
			paths.add(Path.reconstructPath(node));
			return;
		}

		Map<Edge, Node> neighbours = right ? node.getRightNeighbours() : node.getLeftNeighbours();

		if (step == 1) {
			for (Node neighbour : neighbours.values()) {
				neighbour.setPreviousNode(node);
				DFS(neighbour, step + 1, 0, right);
			}
		} else {
			if (neighbours.isEmpty()) {
				DFS(node.getPreviousNode(), step - 1, highestScoreIndex + 1, right);
			} else {
				List<Edge> edges = new ArrayList<>(neighbours.keySet());
				Collections.sort(edges, comparator);

				if (highestScoreIndex == neighbours.size()) {
					return;
				}

				DFS(neighbours.get(edges.get(highestScoreIndex)), step + 1, 0, right);
			}
		}
	}

	@Override
	public Set<List<Node>> findPaths(Graph graph) {
		for (Node node : graph.getNodes().values()) {
			if (node.isAnchor()) {
				DFS(node, 1, 0, true);
				DFS(node, 1, 0, false);
			}
		}

		return paths;
	}

}
