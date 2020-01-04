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
	}

	public String id() {
		return path.get(0).getName() + "_" + path.get(path.size() - 1).getName();
	}

	public int getEstimatedLength() {
		int length = 0;
		int prev = 0;

		for (int i = 0; i < edges.size(); ++i) {
			Edge edge = edges.get(i);
			Node node = path.get(i);

			int qe = edge.getQueryEnd();
			int te = edge.getTargetEnd();
			if (!edge.getQuerySequenceName().equals(node.getName())) {
				int tmp = qe;
				qe = te;
				te = tmp;
			}
			length += qe - prev;
			prev = te;
		}
		return length + path.get(path.size() - 1).getSequenceLength() - prev;
	}

	public double checkSomething() {
		int prev = 0;
		int count = 0;
		for (Edge edge : edges) {
			if (edge.getQueryEnd() < prev) count++;
			prev = edge.getTargetEnd();
		}
		return (double)count / path.size();
	}

	public String getSequence() {
		StringBuilder builder = new StringBuilder();
		int prev = 0;
		for (int i = 0; i < edges.size(); ++i) {
			Edge edge = edges.get(i);
			Node node = path.get(i);

			int qe = edge.getQueryEnd();
			int te = edge.getTargetEnd();
			if (!edge.getQuerySequenceName().equals(node.getName())) {
				int tmp = qe;
				qe = te;
				te = tmp;
			}

			int curr = Math.max(prev, qe);
			builder.append(node.getData().substring(prev, curr));
			prev = te;
		}
		builder.append(path.get(path.size() - 1).getData().substring(prev));
		return builder.toString();
	}
}
