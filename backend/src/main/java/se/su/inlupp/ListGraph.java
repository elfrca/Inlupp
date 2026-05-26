package se.su.inlupp;

import java.util.*;

public class ListGraph<T> implements Graph<T> {

  private Map<T, Set<Edge<T>>> nodes;

  public ListGraph() {

    nodes = new HashMap<>();

  }

  private Edge<T> findEdge(T from, T to) {

    for(Edge<T> edge : nodes.get(from)) {

      if(edge.getDestination().equals(to)) {
        return edge;
      }
    }
    return null;
  }

  @Override
  public void add(T node) {

    if(!nodes.containsKey(node)) {
      nodes.put(node, new HashSet<>());
    }

  }

  @Override
  public void remove(T node) {

    if(!hasNode(node)) {
      throw new NoSuchElementException();
    }

    for(T other : nodes.keySet()) {

      Edge<T> edge = findEdge(other, node);

      if(edge != null) {
        nodes.get(other).remove(edge);
      }

    }
    nodes.remove(node);

  }

  @Override
  public boolean hasNode(T node) {

    return nodes.containsKey(node);

  }

  @Override
  public void connect(T node1, T node2, String name, int weight) {

    if(node1.equals(node2)) {
      throw new IllegalArgumentException();
    }

    if(!hasNode(node1) || !hasNode(node2)) {
      throw new NoSuchElementException();
    }

    if(weight < 0) {
      throw new IllegalArgumentException();
    }

    if(findEdge(node1, node2) != null) {
      throw new IllegalStateException();
    }

    nodes.get(node1).add(new ListEdge(node2, name, weight));
    nodes.get(node2).add(new ListEdge(node1, name, weight));

  }

  @Override
  public void disconnect(T node1, T node2) {

    if(!hasNode(node1) || !hasNode(node2)) {
      throw new NoSuchElementException();
    }
    Edge<T> edge1 = findEdge(node1, node2);
    Edge<T> edge2 = findEdge(node2, node1);

    if(edge1 == null || edge2 == null) {
      throw new IllegalStateException();
    }
    nodes.get(node1).remove(edge1);
    nodes.get(node2).remove(edge2);

  }

  @Override
  public void setConnectionWeight(T node1, T node2, int weight) {

    if(weight < 0) {
      throw new IllegalArgumentException();
    }
    Edge<T> edge1 = getEdgeBetween(node1, node2);
    Edge<T> edge2 = getEdgeBetween(node2, node1);

    if(edge1 == null || edge2 == null) {
      throw new NoSuchElementException();
    }
    edge1.setWeight(weight);
    edge2.setWeight(weight);

  }

  @Override
  public Set<T> getNodes() {

    return new HashSet<>(nodes.keySet());

  }

  @Override
  public Collection<Edge<T>> getEdgesFrom(T node) {

    if(!hasNode(node)) {
      throw new NoSuchElementException();
    }
    return new HashSet<>(nodes.get(node));

  }

  @Override
  public Edge<T> getEdgeBetween(T node1, T node2) {

    if(!hasNode(node1) || !hasNode(node2)) {
      throw new NoSuchElementException();
    }
    return findEdge(node1, node2);

  }

  @Override
  public Iterator<T> iterator() {

    return nodes.keySet().iterator();

  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();

    for(T node : nodes.keySet()) {

      sb.append(node).append(" -> ");

      for(Edge<T> edge : nodes.get(node)) {
        sb.append(edge).append(" ");
      }
      sb.append("\n");

    }
    return sb.toString();

  }

  private class ListEdge implements Edge<T>{

    private T destination;
    private String name;
    private int weight;

    public ListEdge(T destination, String name, int weight){
      this.destination = destination;
      this.name = name;
      this.weight = weight;
    }

    @Override
    public T getDestination() {
      return destination;
    }
    @Override
    public int getWeight() {
      return weight;
    }
    @Override
    public void setWeight(int weight) {

      if(weight < 0) {
        throw new IllegalArgumentException();
      }
      this.weight = weight;
    }
    @Override
    public String getName() {
      return name;
    }
    @Override
    public String toString() {
        return "till " + destination + " med " + name + " tar " + weight;
    }
  }

}

