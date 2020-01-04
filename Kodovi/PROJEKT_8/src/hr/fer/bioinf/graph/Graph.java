package hr.fer.bioinf.graph;

import hr.fer.bioinf.Params;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {

	private Map<String, Node> nodes;

	public Graph() {
		this.nodes = new HashMap<>();
	}

	public void addNode(Node node) {
		this.nodes.put(node.getName(), node);
	}

	public Map<String, Node> getNodes() {
		return nodes;
	}

	public static Graph loadFromFiles(String contigsPath, String readsPath, String contigsReadsOverlapsPath,
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

			Node node = new Node(name, data, anchor);
			graph.addNode(node);
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

			if (querySequenceName.equals(targetSequenceName)) {
				continue;
			}

			Edge edge = new Edge(querySequenceName, queryStart, queryEnd, relativeStrand, targetSequenceName,
					targetStart, targetEnd, numberOfResidueMatches, alignmentBlockLength);

			if (edge.getSequenceIdentity() < Params.SEQUENCE_IDENTITY_CUTOFF) {
				continue;
			}

			Node node = graph.nodes.get(querySequenceName);
			node.setSequenceLength(querySequenceLength);

			Node neighbour = graph.nodes.get(targetSequenceName);
			neighbour.setSequenceLength(targetSequenceLength);

			if (queryStart > targetStart && (querySequenceLength - queryEnd) < (targetSequenceLength - targetEnd)) {
				edge.setQueryOverhang(querySequenceLength - queryEnd);
				edge.setTargetOverhang(targetStart);
				edge.setQueryExtension(queryStart);
				edge.setTargetExtension(targetSequenceLength - targetEnd);

				node.addRightNeighbour(edge, neighbour);
				neighbour.addLeftNeighbour(edge, node);
				continue;
			}

			if (queryStart < targetStart && (querySequenceLength - queryEnd) > (targetSequenceLength - targetEnd)) {
				edge.setQueryOverhang(queryStart);
				edge.setTargetOverhang(targetSequenceLength - targetEnd);
				edge.setQueryExtension(querySequenceLength - queryEnd);
				edge.setTargetExtension(targetStart);

				node.addLeftNeighbour(edge, neighbour);
				neighbour.addRightNeighbour(edge, node);
				continue;
			}
		}
	}

}
