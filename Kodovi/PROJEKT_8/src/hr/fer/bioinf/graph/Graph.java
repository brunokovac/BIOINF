package hr.fer.bioinf.graph;

import hr.fer.bioinf.Params;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Graph {
	public static class NodeDualPair {
		private Node original;
		private Node reversed;

		NodeDualPair(String id, String data, boolean anchor) {
			original = new Node(id, data, false, anchor);
			reversed = new Node(id, new StringBuilder(data).reverse().toString(), true, anchor);
		}

		public Node original() {
			return original;
		}

		public Node reversed() {
			return reversed;
		}
	}

	private static class HalfEdge {
		Node node;
		int start;
		int end;
		int sequenceLength;

		HalfEdge(Node node, int start, int end, int sequenceLength) {
			this.node = node;
			this.start = start;
			this.end = end;
			this.sequenceLength = sequenceLength;
		}
	}

	private Map<String, NodeDualPair> nodes;
	private List<Edge> edges;
	private List<Node> allNodes;

	Graph() {
		nodes = new HashMap<>();
		edges = new ArrayList<>();
		allNodes = new ArrayList<>();
	}

	private void addNode(NodeDualPair nodeDualPair) {
		// assert nodeDualPair.original.id == nodeDualPair.reversed.id
		nodes.put(nodeDualPair.original().getID(), nodeDualPair);
		allNodes.add(nodeDualPair.original());
		allNodes.add(nodeDualPair.reversed());
	}

	public NodeDualPair getNodePair(String id) {
		return nodes.get(id);
	}

	public Collection<Node> getNodes() {
		return this.allNodes;
	}

	public Node getNode(String id, boolean reversed) {
		NodeDualPair nodeDualPair = nodes.get(id);
		if (nodeDualPair == null) return null;
		if (reversed) return nodeDualPair.reversed();
		return nodeDualPair.original();
	}

	private void addEdge(Edge edge) {
		if (edge.getSequenceIdentity() < Params.SEQUENCE_IDENTITY_CUTOFF)
			return;
		edge.from().node.addEdge(edge);
		edges.add(edge);
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public static Graph loadFromFiles(String contigsPath, String readsPath,
																		String contigsReadsOverlapsPath,
																		String contigsContigsOverlapsPath) throws IOException {
		Graph graph = new Graph();

		parseFastaFile(contigsPath, graph, true);
		parseFastaFile(readsPath, graph, false);

		parsePafFile(contigsReadsOverlapsPath, graph);
		parsePafFile(contigsContigsOverlapsPath, graph);

		return graph;
	}

	private static void parseFastaFile(String path, Graph graph, boolean anchor) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(path));
		for (int i = 0, size = lines.size(); i < size; i += 2) {
			String name = lines.get(i).trim().substring(1);
			String data = lines.get(i + 1).trim();
			graph.addNode(new NodeDualPair(name, data, anchor));
		}
	}

	private static void parsePafFile(String path, Graph graph) throws IOException {
		for (String line : Files.readAllLines(Paths.get(path))) {
			String data[] = line.split("\t");

			String querySequenceName = data[0];
			int querySequenceLength = Integer.parseInt(data[1]);
			int queryStart = Integer.parseInt(data[2]); // closed
			int queryEnd = Integer.parseInt(data[3]); // open
			char relativeStrand = data[4].charAt(0);
			String targetSequenceName = data[5];
			int targetSequenceLength = Integer.parseInt(data[6]);
			int targetStart = Integer.parseInt(data[7]); // on original strand
			int targetEnd = Integer.parseInt(data[8]); // on original strand
			int numberOfResidueMatches = Integer.parseInt(data[9]);
			int alignmentBlockLength = Integer.parseInt(data[10]);

			// za dualnost:
			int queryStartInv = querySequenceLength - queryEnd;
			int queryEndInv = querySequenceLength - queryStart;
			int targetStartInv = targetSequenceLength - targetEnd;
			int targetEndInv = targetSequenceLength - targetStart;

			if (querySequenceName.equals(targetSequenceName)) {
				continue;
			}

			NodeDualPair queryNodePair = graph.getNodePair(querySequenceName);
			NodeDualPair targetNodePair = graph.getNodePair(targetSequenceName);

			HalfEdge[] queryHalves = {
					new HalfEdge(queryNodePair.original(), queryStart, queryEnd, querySequenceLength),
					new HalfEdge(queryNodePair.reversed(), queryStartInv, queryEndInv, querySequenceLength)};

			HalfEdge[] targetHalves = {
					new HalfEdge(targetNodePair.original(), targetStart, targetEnd, targetSequenceLength),
					new HalfEdge(targetNodePair.reversed(), targetStartInv, targetEndInv,
							targetSequenceLength)};

			for (HalfEdge query : queryHalves) {
				for (HalfEdge target : targetHalves) {
					char strand = (query.node.isReversed() == target.node.isReversed() ? '+' : '-');
					if (strand == relativeStrand) {
						mergeHalves(graph, query, target, strand,
								numberOfResidueMatches, alignmentBlockLength);
					}
				}
			}
		}
	}

	private static void mergeHalves(Graph graph, HalfEdge query, HalfEdge target, char strand,
																	int numberOfResidueMatches, int alignmentBlockLength) {
		// >>qqqq[qq]q
		//      t[tt]tttttt>>
		if (query.start > target.start &&
				(query.sequenceLength - query.end) < (target.sequenceLength - target.end)) {
			Edge.NodeData from = new Edge.NodeData(query.node, query.start, query.end,
					query.sequenceLength - query.end, query.start);
			Edge.NodeData to = new Edge.NodeData(target.node, target.start, target.end,
					target.start, target.sequenceLength - target.end);
			graph.addEdge(new Edge(from, to, numberOfResidueMatches, alignmentBlockLength));
		}

		//     q[qqq]qqqqq>>
		// >>ttt[ttt]t
		if (query.start < target.start &&
				(query.sequenceLength - query.end) > (target.sequenceLength - target.end)) {
			Edge.NodeData from = new Edge.NodeData(target.node, target.start, target.end,
					target.sequenceLength - target.end, target.start);
			Edge.NodeData to = new Edge.NodeData(query.node, query.start, query.end,
					query.start, query.sequenceLength - query.end);
			graph.addEdge(new Edge(from, to, numberOfResidueMatches, alignmentBlockLength));

		}
	}
}
