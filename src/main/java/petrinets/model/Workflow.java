package petrinets.model;

import exceptions.NoSinkFoundOnWorkflowException;
import exceptions.NoSourceFoundOnWorkflowException;

public class Workflow extends PetriNet {

    private Place source;
    private Place sink;

    public Place getSource() {
        if (source == null) {
            for (Place p : getPlaces()) {
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
            for (Place p : getPlaces()) {
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
