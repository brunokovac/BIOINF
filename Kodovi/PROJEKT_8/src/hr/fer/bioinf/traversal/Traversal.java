package hr.fer.bioinf.traversal;

import java.util.List;

import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.graph.Node;

public interface Traversal {

	List<List<Node>> findPaths(Graph graph);

}