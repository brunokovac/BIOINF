package hr.fer.bioinf.traversal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
			return sequenceIdentityComparison; // TODO add read length comparison
		}
	};

	/**
	 * 
	 * @param node
	 *            current node
	 * @param depth
	 *            how many steps it has taken so far
	 * @param used
	 *            used nodes so far
	 * @param depthRecord
	 *            maps node to depth where it was met
	 * @return null if dead end reached, otherwise a list of following nodes with
	 *         current node added
	 */
	private List<Node> search(Node node, int depth, Set<Node> used, Map<Node, Integer> depthRecord) {
		if (depth >= MAX_DEPTH || depth >= depthRecord.getOrDefault(node, Integer.MAX_VALUE)) {
			return null;
		}
		depthRecord.put(node, depth);
		if (used.contains(node)) {
			return null;
		}
		used.add(node);

		if (node.isAnchor()) {
			System.out.println("\t\tFound anchor: " + node.getName() + " at depth " + depth);
			List<Node> retList = new ArrayList<>();
			retList.add(node);
			return retList;
		}

		Map<Edge, Node> rightNeighbours = node.getRightNeighbours();
		List<Edge> edges = new ArrayList<>(rightNeighbours.keySet());
		edges.sort(MAXIMUM_EXTENSION_SCORE_COMPARATOR.reversed());
		for (Edge edge : edges) {
			if (edge.getSequenceIdentity() < Edge.SEQUENCE_IDENTITY_THRESHOLD) {
				continue;
			}
			Node rightNeighbour = rightNeighbours.get(edge);
			List<Node> searched = search(rightNeighbour, depth + 1, used, depthRecord);
			if (searched != null) {
				searched.add(node);
				depthRecord.put(node, depth);
				return searched;
			}
		}
		used.remove(node);

		return null;
	}

	@Override
	public List<List<Node>> findPaths(Graph graph) {
		Map<String, Node> nodes = graph.getNodes();

		List<List<Node>> paths = new ArrayList<>();
		for (Node node : nodes.values()) {
			if (node.isAnchor()) {
				System.out.println("Anchor node: " + node.getName());
				int anchorCnt = 0;
				int nonAnchorCnt = 0;
				Map<Edge, Node> rightNeighbours = node.getRightNeighbours();
				Set<Node> used = new HashSet<>();
				for (Edge edge : rightNeighbours.keySet()) {
					if (edge.getSequenceIdentity() < Edge.SEQUENCE_IDENTITY_THRESHOLD) {
						continue;
					}
					Node rightNeighbour = rightNeighbours.get(edge);
					List<Node> path = null;
					if (!rightNeighbour.isAnchor()) {
						++anchorCnt;
						System.out.println("\tExploring for neighbor: " + rightNeighbour.getName());
						Map<Node, Integer> depthRecord = new HashMap<>();
						used.add(node);
						depthRecord.put(node, 0);
						path = search(rightNeighbour, 1, used, depthRecord);
					} else {
						++nonAnchorCnt;
					}

					if (path != null) {
						path.add(node);
						paths.add(path);
						System.out.println(
								"\tFound a path of size " + path.size() + "; " + paths.size() + " found so far.");
					}
				}
				System.out.println("\tEligible neighbors: " + anchorCnt);
				System.out.println("\tNon eligible neighbors: " + nonAnchorCnt);
			}
		}

		return paths;
	}

}
