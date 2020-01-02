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

	public String id() {
		return path.get(0).getName() + "_" + path.get(path.size() - 1).getName();
	}

	public int getEstimatedLength() {
		int length = 0;
		int prev = 0;
		for (Edge edge : edges) {
			// U pravilu ocekujemo da ce prev uvijek biti manji od edge.getQueryEnd().
			// Medjutim, u praksi to ne mora biti slucaj. Trenutno taj slucaj zanemarujemo
			// tako da uzmemo max izmedju te dvije vrijednosti, ali mozda bismo trebali
			// dojaviti gresku?
			int curr = Math.max(prev, edge.getQueryEnd());
			length += curr - prev;
			prev = edge.getTargetEnd();
		}
		return length + path.get(path.size() - 1).getSequenceLength() - prev;
	}
}
