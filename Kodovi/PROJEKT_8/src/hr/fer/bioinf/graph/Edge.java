package hr.fer.bioinf.graph;

public class Edge {
	public static class NodeData {
		Node node;
		int start;
		int end;
		int overhang;
		int extension;

		NodeData(Node node, int start, int end, int overhang, int extension) {
			this.node = node;
			this.start = start;
			this.end = end;
			this.overhang = overhang;
			this.extension = extension;

			if (end - start + overhang + extension != node.length()) {
				System.err.println("[ERROR]: Edge.NodeData::ctor() segments don't sum up.");
				System.err.printf("          (%d %d)  O: %d  E: %d,  %d%n", start,end,overhang,extension, node.length());
				System.exit(1);
			}
		}

		public Node node() {
			return node;
		}

		public int end() {
			return end;
		}

		public int start() {
			return start;
		}

		public int overhang() {
			return overhang;
		}

		public int extension() {
			return extension;
		}
	}

	private NodeData from;
	private NodeData to;

	private int numberOfResidueMatches;
	private int alignmentBlockLength;

	private double sequenceIdentity;
	private double overlapScore;
	private double extensionScore;

	Edge(NodeData from, NodeData to, int numberOfResidueMatches, int alignmentBlockLength) {
		this.from = from;
		this.to = to;
		this.numberOfResidueMatches = numberOfResidueMatches;
		this.alignmentBlockLength = alignmentBlockLength;

		initScores();
	}

	public NodeData from() {
		return this.from;
	}

	public NodeData to() {
		return this.to;
	}

	private void initScores() {
		int fromOverlap = from.end - from.start;
		int toOverlap = to.end - to.start;
		sequenceIdentity = (double)numberOfResidueMatches / Math.max(fromOverlap, toOverlap);
		overlapScore = (fromOverlap + toOverlap) * sequenceIdentity / 2;
		extensionScore = overlapScore + to.extension / 2.0 - (fromOverlap + toOverlap) / 2.0;
	}

	public double getSequenceIdentity() {
		return sequenceIdentity;
	}

	public double getOverlapScore() {
		return overlapScore;
	}

	public double getExtensionScore() {
		return extensionScore;
	}
}
