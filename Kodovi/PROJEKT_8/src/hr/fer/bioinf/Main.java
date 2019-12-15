package hr.fer.bioinf;

import java.io.IOException;

import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.traversal.Approach1;
import hr.fer.bioinf.traversal.Traversal;

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
		System.out.println(t.findPaths(graph).size());
//		for (List<Node> l : t.findPaths(graph)) {
//			l.forEach(n -> System.err.print(n.getName() + " "));
//			System.err.println();
//		}
	}

}
