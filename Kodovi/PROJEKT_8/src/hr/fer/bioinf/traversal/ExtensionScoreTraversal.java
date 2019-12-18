package hr.fer.bioinf.traversal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.fer.bioinf.graph.Edge;
import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.graph.Node;

public class ExtensionScoreTraversal implements Traversal {

	private static final int MAX_DEPTH = 700;

	private static final Comparator<Edge> MAXIMUM_EXTENSION_SCORE_COMPARATOR = new Comparator<Edge>() {
		@Override
		public int compare(Edge edge1, Edge edge2) {
			double extensionScore1 = edge1.getExtensionScore();
			double extensionScore2 = edge2.getExtensionScore();
			int extensionScoreComparison = Double.compare(extensionScore1, extensionScore2);
			if (extensionScoreComparison != 0) {
				return Double.compare(extensionScore1, extensionScore2);
			}

			double sequenceIdentity1 = edge1.getSequenceIdentity();
			double sequenceIdentity2 = edge2.getSequenceIdentity();
			int sequenceIdentityComparison = Double.compare(sequenceIdentity1, sequenceIdentity2);
			// if (sequenceIdentityComparison) {
			return sequenceIdentityComparison; // TODO add read length comparison
			// }
		}
	};

	/**
	 * 
	 * @param node
	 *            current node
	 * @param visited
	 *            counts how many steps it took to get to a node; used for decision
	 *            making
	 * @return null if dead end reached, otherwise a list of following nodes with
	 *         current node added
	 */
	private List<Node> search(Node node, int depth, Map<Node, Integer> visited) {
		if (depth >= MAX_DEPTH) {
			return null;
		}

		if (depth >= visited.getOrDefault(node, Integer.MAX_VALUE)) {
			return null;
		}
		visited.put(node, depth);
		
		if (node.isAnchor()) {
			List<Node> retList = new ArrayList<>();
			retList.add(node);
			return retList;
		}

		Map<Edge, Node> rightNeighbours = node.getRightNeighbours();
		List<Edge> edges = new ArrayList<>(rightNeighbours.keySet());
		edges.sort(MAXIMUM_EXTENSION_SCORE_COMPARATOR.reversed());
		for (Edge edge : edges) {
			Node rightNeighbour = rightNeighbours.get(edge);
//			if (depth >= visited.getOrDefault(rightNeighbour, Integer.MAX_VALUE)) {
//				continue;
//			}
//			visited.put(rightNeighbour, depth+1);
			List<Node> searched = search(rightNeighbour, depth+1, visited);
			if (searched != null) {
				searched.add(node);
				return searched;
			}
		}
		return null;
	}

	@Override
	public List<List<Node>> findPaths(Graph graph) {
		Map<String, Node> nodes = graph.getNodes();

		List<List<Node>> paths = new ArrayList<>();
		for (Node node : nodes.values()) {
			if (node.isAnchor()) {
				Map<Edge, Node> rightNeighbours = node.getRightNeighbours();
				Map<Node, Integer> visited = new HashMap<>();
				visited.put(node, 0);
				for (Edge edge : rightNeighbours.keySet()) {
					Node rightNeighbour = rightNeighbours.get(edge);
					List<Node> path = null;
					if (!rightNeighbour.isAnchor()) {
						path = search(rightNeighbour, 1, visited);
					}

					if (path != null) {
						path.add(node);
						paths.add(path);
					}
				}
			}
		}

		return paths;
	}

}
