package petrinets.model;

import java.util.TreeSet;

public abstract class Node implements Comparable<Node> {

    private final String name;
    protected TreeSet<Node> preset;
    protected TreeSet<Node> postset;

    public Node(String name) {
        this.name = name;
        this.preset = new TreeSet<Node>();
        this.postset = new TreeSet<Node>();
    }

    public abstract TreeSet<? extends Node> getPreset();

    public abstract TreeSet<? extends Node> getPostset();

    public void addSuccessor(Node successor) {
        postset.add(successor);
        successor.addPredecessor(this);
    }

    private void addPredecessor(Node predecessor) {
        preset.add(predecessor);
    }

    public int compareTo(Node node) {
        return name.compareTo(node.getName());
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

}
