package hr.fer.bioinf.graph;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private String id;
	private String data;
	private boolean reversed;
	private boolean anchor;

	private List<Edge> edges;

	Node(String id, String data, boolean reversed, boolean anchor) {
		this.id = id;
		this.data = data;
		this.reversed = reversed;
		this.anchor = anchor;
		this.edges = new ArrayList<>();
	}

	public String getID() {
		return id;
	}

	public String getData() {
		return data;
	}

	public boolean isReversed() {
		return reversed;
	}

	public boolean isAnchor() {
		return anchor;
	}

	public int length() {
		return data.length();
	}

	public void addEdge(Edge edge) {
		if (edge.from().node != this) {
			System.err.println("[ERROR] Node::addEdge() received unexpected starting node.");
		}
		edges.add(edge);
	}

	public List<Edge> getEdges() {
		return edges;
	}
}
