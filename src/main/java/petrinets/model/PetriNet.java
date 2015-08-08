package petrinets.model;

import codegeneration.xml.XMLDocument;
import codegeneration.xml.XMLNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class PetriNet {

    private LinkedHashSet<Place> places;
    private LinkedHashSet<Transition> transitions;
    private LinkedHashSet<Place> sortedPlaces;
    private LinkedHashSet<Transition> sortedTransitions;
    private ArrayList<Flow> flows;

    public PetriNet() {
        places = new LinkedHashSet<>();
        transitions = new LinkedHashSet<>();
        flows = new ArrayList<>();
    }

    public void addFlow(Flow flow) {
        flows.add(flow);
        Node source = flow.getSource();
        Node target = flow.getTarget();
        if (flow instanceof PTFlow) {
            places.add((Place) source);
            transitions.add((Transition) target);
        } else {
            transitions.add((Transition) source);
            places.add((Place) target);
        }
        source.addSuccessor(target);
    }

    public LinkedHashSet<Place> getPlaces() {
        if (sortedPlaces == null) {
            Place[] sortedPlacesArray = places.toArray(new Place[places.size()]);
            Arrays.sort(sortedPlacesArray);
            sortedPlaces = new LinkedHashSet<>(Arrays.asList(sortedPlacesArray));
        }
        return sortedPlaces;
    }

    public LinkedHashSet<Transition> getTransitions() {
        if (sortedTransitions == null) {
            Transition[] sortedTransitionsArray = transitions.toArray(new Transition[transitions.size()]);
            Arrays.sort(sortedTransitionsArray);
            sortedTransitions = new LinkedHashSet<>(Arrays.asList(sortedTransitionsArray));
        }
        return sortedTransitions;
    }

    public ArrayList<Flow> getFlows() {
        return flows;
    }

    public XMLDocument toPNML() {
        XMLDocument document = new XMLDocument();
        XMLNode pnml = new XMLNode("pnml");
        XMLNode net = new XMLNode("net");
        for (Place place : getPlaces()) {
            XMLNode placeNode = new XMLNode("place");
            placeNode.addAttribute("id", place.getName());
            net.addChild(placeNode);
        }
        for (Transition transition : getTransitions()) {
            XMLNode transitionNode = new XMLNode("transition");
            transitionNode.addAttribute("id", transition.getName());
            net.addChild(transitionNode);
        }
        for (Flow flow : getFlows()) {
            XMLNode flowNode = new XMLNode("arc");
            flowNode.addAttribute("id", flow.getSource().getName() + "-" + flow.getTarget().getName());
            flowNode.addAttribute("source", flow.getSource().getName());
            flowNode.addAttribute("target", flow.getTarget().getName());
            net.addChild(flowNode);
        }
        pnml.addChild(net);
        document.setRootNode(pnml);
        return document;
    }

    @Override
    public String toString() {
        for (Place p : getPlaces()) {
            for (Transition t : p.getPostset()) {
                System.out.println(p + " --> " + t);
            }
            for (Transition t : p.getPreset()) {
                System.out.println(t + " --> " + p);
            }
        }
        return "";
    }

}
