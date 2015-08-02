package petrinets.model;

import java.util.TreeSet;

public class Transition extends Node {

    public Transition(String name) {
        super(name);
    }

    @Override
    public TreeSet<Place> getPreset() {
        return (TreeSet<Place>) (TreeSet<?>) preset;
    }

    @Override
    public TreeSet<Place> getPostset() {
        return (TreeSet<Place>) (TreeSet<?>) preset;
    }

}
