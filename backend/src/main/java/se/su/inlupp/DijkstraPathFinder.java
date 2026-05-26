package se.su.inlupp;

import java.util.HashMap;
import java.util.*;
public class DijkstraPathFinder<T> implements PathFinder<T> {

    private static class NodeRecord<T> {
        T node;
        int distance;

        NodeRecord(T node, int distance) {
            this.node = node;
            this.distance = distance;
        }
    }

    @Override
    public Path<T> findPath(Graph<T> graph, T from, T to) {
        Map<T, Integer> distances = new HashMap<>();
        Map<T, Edge<T>> previous = new HashMap<>();

        PriorityQueue<NodeRecord<T>> pq =
                new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));

        //alla noder börjar på oändligt
        for (T node : graph.getNodes()) {
            distances.put(node, Integer.MAX_VALUE);
        }

        distances.put(from, 0);
        pq.add(new NodeRecord<>(from, 0));

        while (!pq.isEmpty()) {
            NodeRecord<T> current = pq.poll();

            T currentNode = current.node;
            int currentDistance = current.distance;

            //ignorera gamla dyrare vägar
            if (currentDistance > distances.get(currentNode)) {
                continue;
            }

            // kolla grannar
            for (Edge<T> edge : graph.getEdgesFrom(currentNode)) {
                T neighbor = edge.getDestination();

                int newDistance = currentDistance + edge.getWeight();

                //Hittat bättre väg
                if (newDistance < distances.get(neighbor)) {

                    distances.put(neighbor, newDistance);

                    previous.put(neighbor, edge);

                    pq.add(new NodeRecord<>(neighbor, newDistance));

                }
            }
        }
        //Ingen väg finns
        if (!previous.containsKey(to) && !from.equals(to)){
            return null;
        }

        //Bygger vägen baklänges
        List<Edge<T>> path = new ArrayList<>();

        T current = to;

        while (!current.equals(from)) {
            Edge<T> edge = previous.get(current);

            path.add(edge);

            // hitta föregående nod
            current = findPreviousNode(graph, edge);
        }

        Collections.reverse(path);

        return new SimplePath<>(from, path);

    }
    private T findPreviousNode(Graph<T> graph, Edge<T> targetEdge) {

        for(T node : graph.getNodes()) {

            for (Edge<T> edge : graph.getEdgesFrom(node)) {
                if (edge == targetEdge) {
                    return node;
                }
            }
        }
        return null;
    }
}
