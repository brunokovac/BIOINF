package hr.fer.bioinf.traversal;

import hr.fer.bioinf.graph.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class Consensus {
  Node startNode;
  Node endNode;
  List<TraversalPath> paths;

  public Consensus(Node startNode, Node endNode, List<TraversalPath> paths) {
    this.startNode = startNode;
    this.endNode = endNode;
    // remove duplicates
    this.paths = new ArrayList<>(new HashSet<>(paths));
    // and sort
    this.paths.sort(Comparator.comparingInt(TraversalPath::getEstimatedLength));
  }

  // calculates consensus path
  public TraversalPath calculatePath() {
    int windowSize = 30000;
    int i = 0, j = 0;
    List<List<TraversalPath>> windows = new ArrayList<>();
    while (i < paths.size()) {
      List<TraversalPath> window = new ArrayList<>();
      while (j < paths.size() && paths.get(i).getEstimatedLength() + windowSize > paths.get(j).getEstimatedLength()) {
        window.add(paths.get(j));
        j++;
      }
      windows.add(window);
      i = j;
    }
    List<TraversalPath> best = windows.get(0);
    for (List<TraversalPath> window : windows) {
      if (window.size() > best.size())
        best = window;
    }
    if (best.size() > 5) {
      for (TraversalPath path : best)
        if (path.checkSomething() < 0.001)
          return path;
      return best.get(0);
    }
    return null;
  }
}
