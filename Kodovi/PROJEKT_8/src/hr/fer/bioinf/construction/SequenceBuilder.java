package hr.fer.bioinf.construction;

import hr.fer.bioinf.Params;
import hr.fer.bioinf.consensus.Consensus;
import hr.fer.bioinf.graph.Graph;
import hr.fer.bioinf.graph.Node;
import hr.fer.bioinf.traversal.TraversalPath;

import java.util.*;
import java.util.stream.Collectors;

/** Class that builds resulting DNA sequences based on list of consensus paths. */
public class SequenceBuilder {
  private Graph graph;

  public SequenceBuilder(Graph graph) {
    this.graph = graph;
  }

  /**
   * Builds list of resulting sequences using the following algorithm: First, all consensus paths
   * are sorted by its valid path number. Then the sorted list is iterated and edge connecting two
   * anchoring nodes of the current consensus path is added to the resulting graph only if that
   * action is possible (i.e. if it won't create cycle, or make one node have two possible ingoing
   * or outgoing edges).
   */
  public List<TraversalPath> build(List<Consensus> consensuses) {
    consensuses.sort(Comparator.comparingInt(Consensus::getValidIndex));
    Collections.reverse(consensuses);

    ConstructionGraph constructionGraph = new ConstructionGraph();
    for (Consensus consensus : consensuses) {
      constructionGraph.maybeAddEdge(consensus.getPath());
    }
    return constructionGraph.getPaths();
  }

  public List<TraversalPath> buildUsingConflictIndex(List<Consensus> consensuses) {
    int minValidIndex = consensuses.get(consensuses.size() - 1).getValidIndex() / 20;
    List<Consensus> filteredConsensuses =
        consensuses.stream()
            .filter(consensus -> consensus.getValidIndex() >= minValidIndex)
            .collect(Collectors.toList());
    Collections.reverse(filteredConsensuses);

    Map<Node, List<Consensus>> consensusMapping = splitByStartNode(consensuses);
    List<Consensus> finalConsensuses = new ArrayList<>();

    for (Map.Entry<Node, List<Consensus>> entry : consensusMapping.entrySet()) {
      List<Consensus> list = entry.getValue();
      list.sort(Comparator.comparingInt(Consensus::getValidIndex));
      Collections.reverse(list);
      if (list.size() == 1) {
        finalConsensuses.add(list.get(0));
        continue;
      }
      double conflictIndex = list.get(1).getValidIndex() / (double) list.get(0).getValidIndex();
      if (conflictIndex < Params.CONFLIXT_INDEX_CUTOFF) {
        finalConsensuses.add(list.get(0));
      }
    }
    return build(finalConsensuses);
  }

  private Map<Node, List<Consensus>> splitByStartNode(List<Consensus> consensuses) {
    Map<Node, List<Consensus>> mapping = new HashMap<>();
    for (Consensus consensus : consensuses) {
      Node key = consensus.getPath().from();
      if (!mapping.containsKey(key)) {
        mapping.put(key, new ArrayList<>());
      }
      mapping.get(key).add(consensus);
    }
    return mapping;
  }
}
