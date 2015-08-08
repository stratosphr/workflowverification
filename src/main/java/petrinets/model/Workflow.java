package petrinets.model;

import exceptions.NoSinkFoundOnWorkflowException;
import exceptions.NoSourceFoundOnWorkflowException;

import java.util.LinkedHashSet;

public class Workflow extends PetriNet {

    private Place source;
    private Place sink;

    @Override
    public LinkedHashSet<Place> getPlaces() {
        LinkedHashSet<Place> places = new LinkedHashSet<>();
        places.add(getSource());
        places.add(getSink());
        places.addAll(super.getPlaces());
        return places;
    }

    public Place getSource() {
        if (source == null) {
            for (Place p : super.getPlaces()) {
                if (p.getPreset().isEmpty()) {
                    source = p;
                    return source;
                }
            }
            throw new NoSourceFoundOnWorkflowException();
        } else {
            return source;
        }
    }

    public Place getSink() {
        if (sink == null) {
            for (Place p : super.getPlaces()) {
                if (p.getPostset().isEmpty()) {
                    sink = p;
                    return sink;
                }
            }
            throw new NoSinkFoundOnWorkflowException();
        } else {
            return sink;
        }
    }

}
