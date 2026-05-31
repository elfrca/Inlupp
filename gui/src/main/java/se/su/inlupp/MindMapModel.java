package se.su.inlupp;

import java.util.Set;

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

    public Set<String> getIdeas() {
        return graph.getNodes();
    }

    public void connectIdeas(String from, String to, String relation, int weight) {
        graph.connect(from, to, relation, weight);
    }

    public void disconnectIdeas(String from, String to) {
        graph.disconnect(from, to);
    }

    public Edge<String> getConnection(String from, String to) {
        return graph.getEdgeBetween(from, to);
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

    public Graph<String> getGraph() {
        return graph;
    }
}