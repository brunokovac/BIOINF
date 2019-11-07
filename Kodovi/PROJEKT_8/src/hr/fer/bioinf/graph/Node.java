package hr.fer.bioinf.graph;

import java.util.ArrayList;
import java.util.List;

public class Node {

	private String name;
	private boolean anchor;

	private List<Node> neighbours;

	public Node(String name, boolean anchor) {
		this.name = name;
		this.anchor = anchor;
		this.neighbours = new ArrayList<>();
	}

	public void addNeighbour(Node node) {
		this.neighbours.add(node);
	}

	public String getName() {
		return name;
	}

	public boolean isAnchor() {
		return anchor;
	}

	public List<Node> getNeighbours() {
		return neighbours;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("name=%s, anchor=%s%n", name, anchor));

		for (Node neighbour : neighbours) {
			sb.append(String.format("\tname=%s, anchor=%s%n", neighbour.name, neighbour.anchor));
		}

		return sb.toString();
	}

}
