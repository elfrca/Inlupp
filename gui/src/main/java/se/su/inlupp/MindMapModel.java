package se.su.inlupp;
import java.util.*;
public class MindMapModel {

    private Graph<String> graph;
    private PathFinder<String> pathFinder;

    public MindMapModel() {
        graph = new ListGraph<>();
        pathFinder = new DFSPathFinder<>();
    }

    public void addIdea(String idea) {
        graph.add(idea);
    }

    public void removeIdea(String idea) {
        graph.remove(idea);
    }

    public void connectIdeas(String from, String to, String relation, int weight) {
        graph.connect(from, to, relation, weight);
    }

    public void disconnectIdeas(String from, String to) {
        graph.disconnect(from, to);
    }

    public Path<String> findPath(String from, String to) {
        return pathFinder.findPath(graph, from, to);
    }

    public void useDFS() {
        pathFinder = new DFSPathFinder<>();
    }

    public void useBFS() {
        pathFinder = new BFSPathFinder<>();
    }

    public Set<String> getIdeas() {
        return graph.getNodes();
    }

    public Graph<String> getGraph() {
        return graph;
    }

    public Collection<Edge<String>> getConnections(String idea) {
        return graph.getEdgesFrom(idea);
    }
}