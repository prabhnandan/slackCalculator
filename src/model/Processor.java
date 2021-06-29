package model;

import java.util.ArrayList;

public class Processor {
    private ArrayList<Node> nodesScheduled = new ArrayList<>();
    private int id;

    public Processor(int id) {
        this.id = id;
    }

    public ArrayList<Node> getNodesScheduled() {
        return nodesScheduled;
    }

    public void addNode(Node n) {
        nodesScheduled.add(n);
        nodesScheduled.sort(new NodeSorter());
    }


    public int getID() {
        return this.id;
    }
}
