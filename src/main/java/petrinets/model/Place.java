package petrinets.model;

import java.util.TreeSet;

public class Place extends Node {

    public Place(String name) {
        super(name);
    }

    @Override
    public TreeSet<Transition> getPreset() {
        return (TreeSet<Transition>) (TreeSet<?>) preset;
    }

    @Override
    public TreeSet<Transition> getPostset() {
        return (TreeSet<Transition>) (TreeSet<?>) postset;
    }

}
