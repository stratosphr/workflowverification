package petrinets.model;

import com.sun.istack.internal.NotNull;

import java.util.TreeSet;

public abstract class Node implements Comparable<Node> {

    private final String name;
    protected TreeSet<Node> preset;
    protected TreeSet<Node> postset;

    public Node(String name) {
        this.name = name;
        this.preset = new TreeSet<>();
        this.postset = new TreeSet<>();
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

    public int compareTo(@NotNull Node node) {
        return name.compareTo(node.getName());
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Node && compareTo((Node) object) == 0;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (getName() != null ? getName().hashCode() : 0);
        return hash;
    }

}
