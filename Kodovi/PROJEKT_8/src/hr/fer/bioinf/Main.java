package hr.fer.bioinf;

import java.io.IOException;
import java.util.*;

import hr.fer.bioinf.graph.Edge;
import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.graph.Node;
import hr.fer.bioinf.traversal.Approach2;
import hr.fer.bioinf.traversal.CombinedTraversal;
import hr.fer.bioinf.traversal.Traversal;
import hr.fer.bioinf.traversal.TraversalPath;

import javax.swing.event.TreeExpansionEvent;

public class Main {

	public static void debug(TraversalPath path) {
		List<Node> nodes = path.getPath();
		List<Edge> edges = path.getEdges();
		for (int i = 0; i < edges.size(); ++i) {
			System.out.println(nodes.get(i).getName() + "    -    " + edges.get(i));
		}
		System.out.println(nodes.get(nodes.size() - 1).getName());
	}

	public static void main(String[] args) throws IOException {
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
		System.out.println((System.currentTimeMillis() - t1) + "ms");

		System.out.println(paths.size());

		paths.sort(Comparator.comparingInt(TraversalPath::getEstimatedLength));

		Map<String, List<TraversalPath>> mapa = new TreeMap<>();


		for (TraversalPath path : paths) {
			if (!mapa.containsKey(path.id())) {
				mapa.put(path.id(), new ArrayList<>());
			}
			mapa.get(path.id()).add(path);
		}

		for (Map.Entry<String, List<TraversalPath>> ppp : mapa.entrySet()) {
			System.out.println();
			for (TraversalPath path : ppp.getValue()) {
				System.out.println(ppp.getKey() + " " + path.getEstimatedLength());
			}
		}
	}

}
