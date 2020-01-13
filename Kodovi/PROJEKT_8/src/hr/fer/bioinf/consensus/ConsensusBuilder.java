package hr.fer.bioinf.consensus;

import hr.fer.bioinf.Params;
import hr.fer.bioinf.traversal.TraversalPath;

import java.util.Comparator;
import java.util.List;

public class ConsensusBuilder {
  public static Consensus build(List<TraversalPath> paths) {
    paths.sort(Comparator.comparingInt(TraversalPath::getEstimatedLength));
    int j = 0;
    int best = 0, start = 0;
    for (int i = 0; i < paths.size(); ++i) {
      while (j < paths.size()
          && paths.get(j).getEstimatedLength() - paths.get(i).getEstimatedLength()
              < Params.CONSENSUS_WINDOW_SIZE) {
        j++;
        if (j - i > best) {
          best = j - i;
          start = i;
        }
      }
    }
    TraversalPath bestPath = paths.get(start);
    int end = start;
    for (; end < paths.size(); ++end) {
      if (paths.get(end).getEstimatedLength() - paths.get(start).getEstimatedLength()
          >= Params.CONSENSUS_WINDOW_SIZE) break;
      if (paths.get(end).getEstimatedLength() > bestPath.getEstimatedLength()) {
        bestPath = paths.get(end);
      }
    }
    return new Consensus(bestPath, end - start);
  }
}
