package model;

import java.util.Comparator;

public class NodeSorter implements Comparator<Node> {

    @Override
    public int compare(Node n1, Node n2) {
        return n1.getStartTime().compareTo(n2.getStartTime());
    }
}
