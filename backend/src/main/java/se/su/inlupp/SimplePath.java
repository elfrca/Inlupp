package se.su.inlupp;
import java.util.*;

public class SimplePath<T> implements Path<T>{
    private T start;
    private List<Edge<T>> edges;

    public SimplePath(T start, List<Edge<T>> edges) {
        this.start = start;
        this.edges = edges;
    }

    @Override
    public T getStart() {
        return start;
    }
    @Override
    public T getEnd() {
        if( edges.isEmpty()) {
            return start;
        }
        return edges.get(edges.size() - 1).getDestination();
    }

    @Override
    public int getTotalWeight(){
        int sum = 0;

        for (Edge<T> edge : edges) {
            sum += edge.getWeight();
        }
        return sum;
    }

    @Override
    public List<Edge<T>> getEdges() {
        return edges;
    }

    @Override
    public List<T> getNodes () {
        List<T> nodes = new ArrayList<>();
        nodes.add(start);

        for(Edge<T> edge : edges) {
            nodes.add(edge.getDestination());
        }
        return nodes;
    }

    @Override
    public Iterator<Edge<T>> iterator() {
        return edges.iterator();
    }

    @Override
    public String toString() {
        String result = start.toString();

        for (Edge<T> edge : edges) {
            result += " --(" +
                    edge.getName() +
                    ", " +
                    edge.getWeight() +
                    ")--> " +
                    edge.getDestination();
        }

        result += "\n\nTotal vikt: " + getTotalWeight();

        return result;
    }

}
