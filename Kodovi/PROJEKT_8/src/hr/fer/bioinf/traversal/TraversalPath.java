package hr.fer.bioinf.traversal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hr.fer.bioinf.graph.Edge;
import hr.fer.bioinf.graph.Node;
import hr.fer.bioinf.utils.Hasher;

public class TraversalPath {
	private List<Edge> edges;


	public TraversalPath(List<Edge> edges) {
		this.edges = new ArrayList<>(edges);
	}

	public List<Edge> getEdges() {
		return edges;
	}

	/*
	public static TraversalPath merge(TraversalPath t1, TraversalPath t2) {
		if (!t1.getPath().get(t1.getPath().size() - 1).equals(t2.getPath().get(0)))
			return null;
		List<Node> nodes = new ArrayList<>(t1.path);
		List<Edge> edges = new ArrayList<>(t1.edges);
		for (int i = 0; i < t2.edges.size(); ++i) {
			nodes.add(t2.path.get(i + 1));
			edges.add(t2.edges.get(i));
		}
		return new TraversalPath(nodes, edges);
	}*/

	public String id() {
		Edge first = edges.get(0);
		Edge last = edges.get(edges.size() - 1);
		return first.from().node().getID() + "_" + last.to().node().getID();
	}

	public int getEstimatedLength() {
		int length = 0;
		int prev = 0;

		for (Edge edge : edges) {
			length += edge.from().end() - prev;
			prev = edge.to().end();
		}

		return length + edges.get(edges.size() - 1).to().node().length() - prev;
	}

	/*
	public double checkSomething() {
		int prev = 0;
		int count = 0;
		for (Edge edge : edges) {
			if (edge.getQueryEnd() < prev) count++;
			prev = edge.getTargetEnd();
		}
		return (double)count / path.size();
	}
	*/

	public String getSequence() {
		StringBuilder builder = new StringBuilder();
		int prev = 0;
		for (Edge edge : edges) {
			int curr = Math.max(prev, edge.from().end());
			builder.append(edge.from().node().getData(), prev, curr);  // removed substr
			prev = edge.to().end();
		}
		builder.append(edges.get(edges.size() - 1).to().node().getData().substring(prev));
		return builder.toString();
	}

	@Override
	public int hashCode() {
		Hasher hasher = new Hasher();
		for (Edge edge : edges) {
			hasher.feed(edge.from().node().getID());
			hasher.feed(edge.from().node().isReversed());
			hasher.feed(edge.to().node().getID());
			hasher.feed(edge.to().node().isReversed());
		}
		return hasher.hashCode();
	}
}
