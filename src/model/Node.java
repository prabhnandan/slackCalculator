package model;

import java.util.HashMap;

public class Node {
    HashMap<Node, Integer> predecessors = new HashMap<>();
    HashMap<Node, Integer> successors = new HashMap<>();
    private String id;
    private int weight;
    private int startTime;
    private int endTime;
    private int processor;

    public Node(String id, int weight, int startTime, int processor) {
        this.id = id;
        this.weight = weight;
        this.startTime = startTime;
        this.processor = processor;
        this.endTime = startTime + weight;
    }

    public int getEndTime() {
        return endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<Node, Integer> getPredecessors() {
        return predecessors;
    }

    public void addPredecessor(Node predecessor, Integer weight) {
        this.predecessors.put(predecessor, weight);
    }

    public void addSuccessor(Node successor, Integer weight) {
        this.successors.put(successor, weight);
    }

    public HashMap<Node, Integer> getSuccessors() {
        return successors;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getProcessor() {
        return processor;
    }

    public void setProcessor(int processor) {
        this.processor = processor;
    }
}
