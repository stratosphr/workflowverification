package petrinets.model;

import codegeneration.xml.XMLDocument;
import codegeneration.xml.XMLNode;

import java.util.ArrayList;
import java.util.TreeSet;

public class PetriNet {

    private TreeSet<Place> places;
    private TreeSet<Transition> transitions;
    private ArrayList<Flow> flows;

    public PetriNet() {
        places = new TreeSet<Place>();
        transitions = new TreeSet<Transition>();
        flows = new ArrayList<Flow>();
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

    public TreeSet<Place> getPlaces() {
        return places;
    }

    public TreeSet<Transition> getTransitions() {
        return transitions;
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
