package hr.fer.bioinf;

import java.io.IOException;
import java.util.List;

import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.traversal.Approach1;
import hr.fer.bioinf.traversal.Traversal;
import hr.fer.bioinf.traversal.TraversalPath;

public class Main {

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

		Traversal t = new Approach1();
		long t1 = System.currentTimeMillis();
		List<TraversalPath> paths = t.findPaths(graph);
		System.out.println((System.currentTimeMillis() - t1) + "ms");
		System.out.println(paths.size());
		paths.forEach(p -> {
			System.out.print(p.getPath().size() + " " + p.getEdges().size() + " ");
			p.getPath().forEach(n -> System.out.print(n.getName() + " "));
			System.out.println();
		});
	}

}
