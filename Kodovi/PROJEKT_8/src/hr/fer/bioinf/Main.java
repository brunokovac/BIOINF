package hr.fer.bioinf;

import java.io.IOException;

import hr.fer.bioinf.graph.Graph;

public class Main {

	public static void main(String[] args) throws IOException {
		String contigsPath = args[0];
		String readsPath = args[1];
		String contigsReadsOverlapsPath = args[2];
		String contigsContigsOverlapsPath = args[3];

		Graph graph = Graph.loadFromFiles(contigsPath, readsPath, contigsReadsOverlapsPath, contigsContigsOverlapsPath);

		System.err.println(graph.getNodes().values());
	}

}
