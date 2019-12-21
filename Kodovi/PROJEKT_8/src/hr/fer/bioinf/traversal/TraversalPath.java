package hr.fer.bioinf.traversal;

import java.util.ArrayList;
import java.util.List;

import hr.fer.bioinf.graph.Edge;
import hr.fer.bioinf.graph.Node;

public class TraversalPath {

	private List<Node> path;
	private List<Edge> edges;

	public TraversalPath(List<Node> path, List<Edge> edges) {
		this.path = new ArrayList<>(path);
		this.edges = new ArrayList<>(edges);
	}

	public List<Node> getPath() {
		return path;
	}

	public void setPath(List<Node> path) {
		this.path = path;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}

}
