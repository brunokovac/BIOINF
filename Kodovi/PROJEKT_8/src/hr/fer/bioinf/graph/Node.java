package hr.fer.bioinf.graph;

import java.util.HashMap;
import java.util.Map;

public class Node {

	private String name;
	private boolean anchor;
	private int sequenceLength;

	private Map<Edge, Node> leftNeighbours;
	private Map<Edge, Node> rightNeighbours;

	private Node previousNode;

	public Node(String name, boolean anchor) {
		this.name = name;
		this.anchor = anchor;
		this.leftNeighbours = new HashMap<>();
		this.rightNeighbours = new HashMap<>();
	}

	public void addLeftNeighbour(Edge edge, Node node) {
		this.leftNeighbours.put(edge, node);
	}

	public void addRightNeighbour(Edge edge, Node node) {
		this.rightNeighbours.put(edge, node);
	}

	public String getName() {
		return name;
	}

	public boolean isAnchor() {
		return anchor;
	}

	public int getSequenceLength() {
		return sequenceLength;
	}

	public void setSequenceLength(int sequenceLength) {
		this.sequenceLength = sequenceLength;
	}

	public Map<Edge, Node> getLeftNeighbours() {
		return leftNeighbours;
	}

	public Map<Edge, Node> getRightNeighbours() {
		return rightNeighbours;
	}

	public Node getPreviousNode() {
		return previousNode;
	}

	public void setPreviousNode(Node previousNode) {
		this.previousNode = previousNode;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("name=%s, anchor=%s, length=%d%n", name, anchor, sequenceLength));

		sb.append(String.format("Left:%n"));
		for (Map.Entry<Edge, Node> neighbour : leftNeighbours.entrySet()) {
			sb.append(String.format("%s %s%n", neighbour.getValue().getName(), neighbour.getKey()));
		}

		sb.append(String.format("%nRight:%n"));
		for (Map.Entry<Edge, Node> neighbour : rightNeighbours.entrySet()) {
			sb.append(String.format("%s %s\n", neighbour.getValue().getName(), neighbour.getKey()));
		}

		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (anchor ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + sequenceLength;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (anchor != other.anchor)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sequenceLength != other.sequenceLength)
			return false;
		return true;
	}

}
