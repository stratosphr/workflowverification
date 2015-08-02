package petrinets;

import exceptions.InvalidWorkflowException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import petrinets.model.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class PIPEParser {

    public static Workflow parse(File pnmlFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //TODO : Set validating to true for PIPEParser
            //factory.setValidating(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new WorkflowParserErrorHandler());
            Document document = builder.parse(pnmlFile);
            NodeList nodes = document.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node root = nodes.item(i);
                if (root.getNodeType() == Node.ELEMENT_NODE) {
                    if (root.getNodeName().equals("pnml")) {
                        NodeList childNodes = root.getChildNodes();
                        for (int j = 0; j < childNodes.getLength(); j++) {
                            Node child = childNodes.item(j);
                            if (child.getNodeType() == Node.ELEMENT_NODE) {
                                if (child.getNodeName().equals("net")) {
                                    HashMap<String, Place> places = new HashMap<>();
                                    HashMap<String, Transition> transitions = new HashMap<>();
                                    Workflow workflow = new Workflow();
                                    NodeList objects = child.getChildNodes();
                                    for (int k = 0; k < objects.getLength(); k++) {
                                        Node object = objects.item(k);
                                        if (object.getNodeType() == Node.ELEMENT_NODE) {
                                            NamedNodeMap attributes = object.getAttributes();
                                            switch (object.getNodeName()) {
                                                case "place":
                                                    String placeName = attributes.getNamedItem("id").getNodeValue();
                                                    places.put(placeName, new Place(placeName));
                                                    break;
                                                case "transition":
                                                    String transitionName = attributes.getNamedItem("id").getNodeValue();
                                                    transitions.put(transitionName, new Transition(transitionName));
                                                    break;
                                                case "arc":
                                                    String sourceName = attributes.getNamedItem("source").getNodeValue();
                                                    String targetName = attributes.getNamedItem("target").getNodeValue();
                                                    if (places.containsKey(sourceName) && transitions.containsKey(targetName)) {
                                                        workflow.addFlow(new PTFlow(places.get(sourceName), transitions.get(targetName)));
                                                    } else if (transitions.containsKey(sourceName) && places.containsKey(targetName)) {
                                                        workflow.addFlow(new TPFlow(transitions.get(sourceName), places.get(targetName)));
                                                    } else {
                                                        throw new InvalidWorkflowException("The workflow arc with source \"" + sourceName + "\" and target \"" + targetName + "\" is invalid : either the source or the target was not declared as a node before or they have the same type.");
                                                    }
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    System.out.println(workflow);
                                    System.out.println(workflow.toPNML());
                                    return workflow;
                                }
                            }
                        }
                    }
                }
            }
            throw new InvalidWorkflowException("Should never happen.");
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static class WorkflowParserErrorHandler implements ErrorHandler {

        @Override
        public void warning(SAXParseException e) throws SAXException {
            throw new InvalidWorkflowException(e.getMessage());
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            throw new InvalidWorkflowException(e.getMessage());
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            throw new InvalidWorkflowException(e.getMessage());
        }

    }

}
