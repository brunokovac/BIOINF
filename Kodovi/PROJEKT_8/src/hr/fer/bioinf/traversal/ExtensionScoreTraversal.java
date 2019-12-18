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

	private List<Node> search(Node node, Set<Edge> used) {
		int depth = used.size();
		if (depth >= MAX_DEPTH) {
			return null;
		}

		if (node.isAnchor()) {
			List<Node> retList = new ArrayList<>();
			retList.add(node);
			return retList;
		}

		Map<Edge, Node> rightNeighbours = node.getRightNeighbours();
		List<Edge> edges = new ArrayList<>(rightNeighbours.keySet());
		edges.sort(MAXIMUM_EXTENSION_SCORE_COMPARATOR.reversed());
		for (Edge edge : edges) {
			if (used.contains(edge)) {
				continue;
			}
			Node otherNode = rightNeighbours.get(edge);
			used.add(edge);
			List<Node> searched = search(otherNode, used);
			if (searched != null) {
				searched.add(node);
				return searched;
			} else {
				// used.remove(edge);				
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
//				System.out.println("anchor:" + node.getName());
				Map<Edge, Node> rightNeighbours = node.getRightNeighbours();
				for (Edge edge : rightNeighbours.keySet()) {
					Node rightNeighbour = rightNeighbours.get(edge);
					List<Node> path = null;
					if (!rightNeighbour.isAnchor()) {
						Set<Edge> used = new HashSet<>();
						used.add(edge);
//						System.out.println("\tadj:" + rightNeighbour.getName());
						path = search(rightNeighbour, used);
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
