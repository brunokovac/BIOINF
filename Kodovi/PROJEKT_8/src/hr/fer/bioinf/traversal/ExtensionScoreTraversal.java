package hr.fer.bioinf.traversal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
			if (extensionScore1 != extensionScore2) {
				return Double.compare(extensionScore1, extensionScore2);
			}
			double sequenceIdentity1 = edge1.getSequenceIdentity();
			double sequenceIdentity2 = edge2.getSequenceIdentity();
			return Double.compare(sequenceIdentity1, sequenceIdentity2);
		}
	};
	
	private void search(List<Node> path) {
		int depth = path.size();
		if (depth >= MAX_DEPTH) {
			return;
		}
		
		Node node = path.get(depth - 1);
		Map<Edge, Node> rightNeighbours = node.getRightNeighbours();
		Edge maxEdge = Collections.max(rightNeighbours.keySet(), MAXIMUM_EXTENSION_SCORE_COMPARATOR);
		Node maxNode = rightNeighbours.get(maxEdge);
		path.add(maxNode);
		if (maxNode.isAnchor()) {
			return;
		}
		search(path);
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
						search(path);
					}
					paths.add(path);
				}
			}
		}
		
		return paths;
	}

}
