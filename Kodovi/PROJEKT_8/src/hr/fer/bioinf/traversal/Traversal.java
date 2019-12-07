package hr.fer.bioinf.traversal;

import java.util.List;
import java.util.Set;

import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.graph.Node;

public interface Traversal {

	Set<List<Node>> findPaths(Graph graph);

}