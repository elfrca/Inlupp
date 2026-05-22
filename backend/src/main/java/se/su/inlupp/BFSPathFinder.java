//HALA1024

package se.su.inlupp;
import java.util.*;

public class BFSPathFinder<T> implements PathFinder<T> {

  public BFSPathFinder() {

  }

  @Override
  public Path<T> findPath(Graph<T> graph, T from, T to) {

    if (!graph.hasNode(from) || !graph.hasNode(to)) {
      return null;
    }

    Queue<T> queue = new LinkedList<>();
    Set<T> visited = new HashSet<>();
    Map<T, T> previous = new HashMap<>();

    queue.add(from);
    visited.add(from);

    while (!queue.isEmpty()) {
      T current = queue.poll();

      if (current.equals(to)) {
        break;
      }

      for (Edge<T> edge : graph.getEdgesFrom(current)) {
        T neighbor = edge.getDestination();

        if (!visited.contains(neighbor)) {
          visited.add(neighbor);
          previous.put(neighbor, current);
          queue.add(neighbor);
        }
      }
    }
    if (!from.equals(to) && !previous.containsKey(to)) {
      return null;
    }

    List<Edge<T>> edges = new ArrayList<>();
    T current = to;

    while (!current.equals(from)) {
      T prev = previous.get(current);
      Edge<T> edge = graph.getEdgeBetween(prev, current);
      edges.add(0, edge);
      current = prev;
    }

    return new SimplePath<>(from, edges);

  }
}