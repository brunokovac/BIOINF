package hr.fer.bioinf.construction;

import hr.fer.bioinf.graph.Node;
import hr.fer.bioinf.traversal.TraversalPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstructionGraph {
  private class Entry {
    Node node;
    TraversalPath edge;

    Entry(Node node, TraversalPath edge) {
      this.node = node;
      this.edge = edge;
    }
  }

  Map<Node, Entry> next = new HashMap<>();
  Map<Node, Entry> prev = new HashMap<>();

  private boolean cycleCheck(Node from, Node to) {
    Node curr = to;
    while (next.containsKey(curr)) {
      curr = next.get(curr).node;
      if (curr.equals(from)) return false;
    }
    return true;
  }

  public void maybeAddEdge(TraversalPath edge) {
    Node from = edge.from();
    Node to = edge.to();
    if (next.containsKey(from) || prev.containsKey(to) || !cycleCheck(from, to)) {
      return;
    }
    next.put(from, new Entry(to, edge));
    prev.put(to, new Entry(from, edge));
  }

  public List<TraversalPath> getPaths() {
    List<TraversalPath> paths = new ArrayList<>();
    for (Node node : next.keySet()) {
      if (prev.containsKey(node)) continue;
      TraversalPath path = next.get(node).edge;
      node = next.get(node).node;
      while (next.containsKey(node)) {
        Entry entry = next.get(node);
        node = entry.node;
        path = path.concat(entry.edge);
      }
      paths.add(path);
    }
    return paths;
  }
}
