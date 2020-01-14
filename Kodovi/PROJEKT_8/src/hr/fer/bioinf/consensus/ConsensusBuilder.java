package hr.fer.bioinf.consensus;

import hr.fer.bioinf.Params;
import hr.fer.bioinf.traversal.TraversalPath;

import java.util.Comparator;
import java.util.List;

public class ConsensusBuilder {
  public static Consensus build(List<TraversalPath> paths) {
    paths.sort(Comparator.comparingInt(TraversalPath::getEstimatedLength));
    int j = 0;
    int best = 0, start = 0, end = 0;
    for (int i = 0; i < paths.size(); ++i) {
      while (j < paths.size()
          && paths.get(j).getEstimatedLength() - paths.get(i).getEstimatedLength()
              < Params.CONSENSUS_WINDOW_SIZE) {
        j++;
        if (j - i > best) {
          best = j - i;
          start = i;
          end = j;
        }
      }
    }
    return new Consensus(paths.get((start + end) / 2), end - start);
  }
}
