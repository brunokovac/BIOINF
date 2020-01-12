package hr.fer.bioinf.construction;

import hr.fer.bioinf.consensus.Consensus;
import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.graph.Node;
import hr.fer.bioinf.traversal.TraversalPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SequenceBuilder {
  Graph graph;

  public SequenceBuilder(Graph graph) {
    this.graph = graph;
  }

  public List<TraversalPath> build(List<Consensus> consensuses) {
    // discard all consensuses path with valid index less than 20 percent of the highest one
    int minValidIndex = consensuses.get(consensuses.size() - 1).getValidIndex() / 8;
    List<Consensus> filteredConsensuses =
        consensuses.stream()
            .filter(consensus -> consensus.getValidIndex() >= minValidIndex)
            .collect(Collectors.toList());
    Collections.reverse(filteredConsensuses);

    ConstructionGraph constructionGraph = new ConstructionGraph();
    for (Consensus consensus : filteredConsensuses) {
      constructionGraph.maybeAddEdge(consensus.getPath());
    }
    return constructionGraph.getPaths();
  }
}
