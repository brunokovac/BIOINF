package hr.fer.bioinf;

import java.io.IOException;

import hr.fer.bioinf.graph.Graph;

public class Main {

	public static void main(String[] args) throws IOException {
//		String contigsPath = args[0];
//		String readsPath = args[1];
//		String contigsReadsOverlapsPath = args[2];
//		String contigsContigsOverlapsPath = args[3];

		String contigsPath = "ecoli_test_contigs.fasta";
		String readsPath = "ecoli_test_reads.fasta";
		String contigsReadsOverlapsPath = "ecoli_test_overlaps.paf";
		String contigsContigsOverlapsPath = "ecoli_test_overlaps.paf";

		Graph graph = Graph.loadFromFiles(contigsPath, readsPath, contigsReadsOverlapsPath, contigsContigsOverlapsPath);

		System.err.println(graph.getNodes().get("ctg1"));
	}

}
