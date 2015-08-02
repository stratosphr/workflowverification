package petrinets.model;

public abstract class Flow {

    private final Node source;
    private final Node target;

    public Flow(Node source, Node target) {
        this.source = source;
        this.target = target;
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return source + " --> " + target;
    }

}
