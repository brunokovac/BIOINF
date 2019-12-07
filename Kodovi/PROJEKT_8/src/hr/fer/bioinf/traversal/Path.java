package hr.fer.bioinf.traversal;

import java.util.ArrayList;
import java.util.List;

import hr.fer.bioinf.graph.Node;

public class Path {

	private static List<Node> nodes = new ArrayList<>();

	private static void reconstruct(Node node) {
		if (node.getPreviousNode() == null) {
			nodes.add(node);
			return;
		}

		reconstruct(node.getPreviousNode());
		nodes.add(node);
	}

	public static List<Node> reconstructPath(Node endNode) {
		reconstruct(endNode);
		return nodes;
	}

}
