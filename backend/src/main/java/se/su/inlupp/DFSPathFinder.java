package se.su.inlupp;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class DFSPathFinder<T> implements PathFinder<T> {

  @Override
  public Path<T> findPath(Graph<T> graph, T from, T to) {
    // Håller koll på vilka noder vi redan besökt
    Set<T> visited = new HashSet<>();

    // Startar DFS
    List<Edge<T>> result = dfs(graph, from, to, visited);

    // Ingen väg hittades
    if(result == null) {
      return null;
    }
    return new SimplePath<>(from, result);
  }


  private List<Edge<T>> dfs(Graph<T> graph, T current, T goal, Set<T> visited) {
    visited.add(current);
    if(current.equals(goal)) {
      return new ArrayList<>(); //Fattar inte
    }
    // Gå igenom alla edges från current
    for (Edge<T> edge : graph.getEdgesFrom(current)) {

      // Hämta grannen/destinationen
      T neighbor = edge.getDestination();


      // Undvik cykler
      if (!visited.contains(neighbor)) {
        // Gå djupare rekursivt
        List<Edge<T>> path = dfs(graph, neighbor, goal, visited);

        // Om väg hittades
        if ( path != null ) {

          // Lägg current först i vägen
          path.add(0, edge);
          return path;

          }
        }
      }
      // Ingen väg hittades härifrån
      return null;
    }
  }




