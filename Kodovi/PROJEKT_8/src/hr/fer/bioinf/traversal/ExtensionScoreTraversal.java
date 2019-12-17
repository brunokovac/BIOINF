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

public class ExtensionScoreTraversal implements Traversal {

	private static final int MAX_DEPTH = 700;
	
	private static final Comparator<Edge> MAXIMUM_EXTENSION_SCORE_COMPARATOR = new Comparator<Edge>() {
		@Override
		public int compare(Edge edge1, Edge edge2) {
			double extensionScore1 = edge1.getExtensionScore();
			double extensionScore2 = edge2.getExtensionScore();
			if (extensionScore1 != extensionScore2) {
				return Double.compare(extensionScore1, extensionScore2);
			}
			double sequenceIdentity1 = edge1.getSequenceIdentity();
			double sequenceIdentity2 = edge2.getSequenceIdentity();
			return Double.compare(sequenceIdentity1, sequenceIdentity2);
		}
	};
	
	private void search(List<Node> path, Set<Node> visited) {
		Node node = path.get(path.size() - 1);
		for (int depth = 0; depth < MAX_DEPTH; ++depth) {
			Map<Edge, Node> rightNeighbours = node.getRightNeighbours();
			Edge maxEdge = null;
			Node maxNode = null;
			for (Edge edge : rightNeighbours.keySet()) {
				if (maxEdge == null || MAXIMUM_EXTENSION_SCORE_COMPARATOR.compare(edge, maxEdge) > 0) {
					Node rightNeighbour = rightNeighbours.get(edge);
					if (!visited.contains(rightNeighbour)) {
						maxEdge = edge;
						maxNode = rightNeighbour;
					}
				}
			}
			path.add(maxNode);
			if (maxNode.isAnchor()) {
				return;
			}
		}
	}
	
	@Override
	public List<List<Node>> findPaths(Graph graph) {
		Map<String, Node> nodes = graph.getNodes();
		
		List<List<Node>> paths = new ArrayList<>();
		for (Node node : nodes.values()) {
			if (node.isAnchor()) {
				for (Node rightNeighbour : node.getRightNeighbours().values()) {
					List<Node> path = new ArrayList<>();
					path.add(node);
					path.add(rightNeighbour);
					if (!rightNeighbour.isAnchor()) {
						Set<Node> visited = new HashSet<>();
						visited.add(node);
						visited.add(rightNeighbour);
						search(path, visited);
					}
					
					// Add path if first and last element are anchors.
					if (path.get(path.size()-1).isAnchor()) {
						paths.add(path);						
					}
				}
			}
		}
		
		return paths;
	}

}
