package hr.fer.bioinf.consensus;

import hr.fer.bioinf.Params;
import hr.fer.bioinf.traversal.TraversalPath;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ConsensusBuilder {
  public static Consensus build(List<TraversalPath> paths) {
    paths.sort(Comparator.comparingInt(TraversalPath::getEstimatedLength));
    int sz = 100;
    int j = 0;
    int best = 0, start = 0;
    for (int i = 0; i < paths.size(); ++i) {
      while (j < paths.size() && paths.get(j).getEstimatedLength() - paths.get(i).getEstimatedLength() < sz) {
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
      if (paths.get(end).getEstimatedLength() - paths.get(start).getEstimatedLength() >= sz) break;
      if (paths.get(end).getEstimatedLength() > bestPath.getEstimatedLength()) {
        bestPath = paths.get(end);
      }
    }
    return new Consensus(bestPath, end - start);
  }

  /*
  private static List<List<TraversalPath>> findGroups(List<TraversalPath> paths) {
    List<Group> windows = Windows.split(paths, Params.CONSENSUS_WINDOW_SIZE);
    List<Integer> lengths = windows.stream().map(window -> window.getPaths().size()).collect(Collectors.toList());
    int lastPeak = lengths.get(lengths.size() - 1);
    for (int i = lengths.size() - 1; i > 0; --i) {
      if (lengths.get(i) > lengths.get(i + 1) && lengths.get(i) > lengths.get(i - 1)) {
        lastPeak = i;
      }
      if (lengths.get(i) < lengths.get(i + 1) && lengths.get(i) < lengths.get(i - 1)) {
        // valley window
        if (windows.get(i).highestFrequency() < windows.get(lastPeak).highestFrequency())
      }
    }
    for (Group window : windows) {
      if (window.)
    }
  }*/
}
