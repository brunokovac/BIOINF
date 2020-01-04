package hr.fer.bioinf;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;

import hr.fer.bioinf.graph.Edge;
import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.graph.Node;
import hr.fer.bioinf.traversal.*;

import javax.swing.event.TreeExpansionEvent;

public class Main {

	public static void debug(TraversalPath path) {
		List<Node> nodes = path.getPath();
		List<Edge> edges = path.getEdges();
		for (int i = 0; i < edges.size(); ++i) {
			System.err.println(nodes.get(i).getName() + "    -    " + edges.get(i));
		}
		System.err.println(nodes.get(nodes.size() - 1).getName());
	}

	public static void main(String[] args) throws IOException {
		Params.init(args);

//		String contigsPath = args[0];
//		String readsPath = args[1];
//		String contigsReadsOverlapsPath = args[2];
//		String contigsContigsOverlapsPath = args[3];

		String contigsPath = "ecoli_test_contigs.fasta";
		String readsPath = "ecoli_test_reads.fasta";
		String contigsReadsOverlapsPath = "reads_contigs_overlaps.paf";
		String contigsContigsOverlapsPath = "reads_reads_overlaps.paf";

		Graph graph = Graph.loadFromFiles(contigsPath, readsPath, contigsReadsOverlapsPath, contigsContigsOverlapsPath);

		Traversal t = new CombinedTraversal();
		long t1 = System.currentTimeMillis();
		List<TraversalPath> paths = t.findPaths(graph);
		System.err.println((System.currentTimeMillis() - t1) + "ms");

		System.err.println(paths.size());

		paths.sort(Comparator.comparingInt(TraversalPath::getEstimatedLength));

		Map<String, List<TraversalPath>> mapa = new TreeMap<>();


		for (TraversalPath path : paths) {
			if (!mapa.containsKey(path.id())) {
				mapa.put(path.id(), new ArrayList<>());
			}
			mapa.get(path.id()).add(path);
		}

		Map<String, Consensus> consensusMap = new HashMap<>();

		for (Map.Entry<String, List<TraversalPath>> ppp : mapa.entrySet()) {
			System.err.println();
			List<Node> nodes = ppp.getValue().get(0).getPath();
			Node start = nodes.get(0);
			Node end = nodes.get(nodes.size() - 1);
			Consensus consensus = new Consensus(start, end, ppp.getValue());
			for (TraversalPath path : ppp.getValue()) {
				System.err.println(ppp.getKey() + "  " + path.getEstimatedLength() +
						"  (" + String.format("%.6f", path.checkSomething()) + ")    (nodes: " +
						path.getPath().size() + ")");
			}
			consensusMap.put(ppp.getKey(), consensus);
			System.err.println(ppp.getKey() + "  " + consensus.calculatePath());
		}

		TraversalPath output = TraversalPath.merge(consensusMap.get("ctg1_ctg2").calculatePath(),
				consensusMap.get("ctg2_ctg3").calculatePath());
		System.err.println(output.getEstimatedLength());

		System.out.println(">output");
		System.out.println(output.getSequence());

		System.err.println(output.checkSomething());


		System.err.println();
		System.err.println();

		for (int i = 0; i < output.getEdges().size(); ++i) {
			Edge edge = output.getEdges().get(i);
			System.err.printf("%s   (query: %d %d)   (target: %d %d)   (OH: %d %d)  (names: %s %s) %s%n",
					output.getPath().get(i).getName(), edge.getQueryStart(), edge.getQueryEnd(), edge.getTargetStart(), edge.getTargetEnd(),
					edge.getQueryOverhang(), edge.getTargetOverhang(), edge.getRelativeStrand(),
					edge.getQuerySequenceName(), edge.getTargetSequenceName());
		}
	}

}
