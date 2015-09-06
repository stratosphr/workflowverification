package petrinets.parsers;

import exceptions.InvalidWorkflowException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import petrinets.model.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class HadaraParser implements IWorkflowParser {

    @Override
    public Workflow parse(File hadaraFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //TODO : Set validating to true for HadaraParser
            //factory.setValidating(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new WorkflowParserErrorHandler());
            Document document = builder.parse(hadaraFile);
            NodeList nodes = document.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node root = nodes.item(i);
                if (root.getNodeType() == Node.ELEMENT_NODE) {
                    if (root.getNodeName().equals("CGraph")) {
                        NodeList objects = root.getChildNodes();
                        HashMap<String, Place> places = new HashMap<>();
                        HashMap<String, Transition> transitions = new HashMap<>();
                        Workflow workflow = new Workflow();
                        for (int j = 0; j < objects.getLength(); j++) {
                            Node object = objects.item(j);
                            if (object.getNodeType() == Node.ELEMENT_NODE) {
                                NamedNodeMap attributes = object.getAttributes();
                                switch (object.getNodeName()) {
                                    case "CGraph":
                                        String nodeName = attributes.getNamedItem("Label").getNodeValue();
                                        if (attributes.getNamedItem("Type").getNodeValue().equals("Place")) {
                                            places.put(nodeName, new Place(nodeName));
                                        } else {
                                            transitions.put(nodeName, new Transition(nodeName));
                                        }
                                        break;
                                    case "CArc":
                                        String sourceName = attributes.getNamedItem("Source").getNodeValue();
                                        String targetName = attributes.getNamedItem("Target").getNodeValue();
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
                        return workflow;
                    }
                }
            }
            throw new InvalidWorkflowException("Should never happen.");
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
