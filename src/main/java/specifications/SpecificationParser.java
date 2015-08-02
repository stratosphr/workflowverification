package specifications;

import exceptions.InvalidSpecificationException;
import exceptions.InvalidWorkflowException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import specifications.model.formulas.*;
import specifications.model.Specification;
import specifications.model.SpecificationType;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SpecificationParser {

    public static Specification parse(File specificationFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new SpecificationParserErrorHandler());
            Document document = builder.parse(specificationFile);
            NodeList nodes = document.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node root = nodes.item(i);
                if (root.getNodeType() == Node.ELEMENT_NODE) {
                    if (root.getNodeName().equals("specification")) {
                        NamedNodeMap attributes = root.getAttributes();
                        String specificationName = attributes.getNamedItem("name").getNodeValue();
                        SpecificationType specificationType = (attributes.getNamedItem("type").getNodeValue().equals("may")) ? SpecificationType.MAY : SpecificationType.MUST;
                        NodeList childNodes = root.getChildNodes();
                        for (int j = 0; j < childNodes.getLength(); j++) {
                            Node child = childNodes.item(j);
                            if (child.getNodeType() == Node.ELEMENT_NODE) {
                                return new Specification(specificationName, specificationType, getFormula(child));
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

    private static Formula getFormula(Node child) {
        String elementName = child.getNodeName();
        switch (elementName) {
            case "transition":
                String transitionName = child.getAttributes().getNamedItem("name").getNodeValue();
                return new TransitionFormula(transitionName);
            case "conjunction":
                NodeList conjunctionChildrenNodes = child.getChildNodes();
                ArrayList<Formula> conjunctionChildren = new ArrayList<>();
                for (int i = 0; i < conjunctionChildrenNodes.getLength(); i++) {
                    Node childNode = conjunctionChildrenNodes.item(i);
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        conjunctionChildren.add(getFormula(childNode));
                    }
                }
                return new ConjunctionFormula(conjunctionChildren);
            case "disjunction": {
                NodeList disjunctionChildrenNodes = child.getChildNodes();
                ArrayList<Formula> disjunctionChildren = new ArrayList<>();
                for (int i = 0; i < disjunctionChildrenNodes.getLength(); i++) {
                    Node childNode = disjunctionChildrenNodes.item(i);
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        disjunctionChildren.add(getFormula(childNode));
                    }
                }
                return new DisjunctionFormula(disjunctionChildren);
            }
            case "negation":
                NodeList negationChildrenNodes = child.getChildNodes();
                for (int i = 0; i < negationChildrenNodes.getLength(); i++) {
                    Node childNode = negationChildrenNodes.item(i);
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        return new NegationFormula(getFormula(childNode));
                    }
                }
                break;
            default:
                throw new InvalidSpecificationException("Should never happen.");
        }
        throw new InvalidSpecificationException("Should never happen.");
    }

    private static class SpecificationParserErrorHandler implements ErrorHandler {

        public void warning(SAXParseException e) throws SAXException {
            throw new InvalidSpecificationException(e.getMessage());
        }

        public void error(SAXParseException e) throws SAXException {
            throw new InvalidSpecificationException(e.getMessage());
        }

        public void fatalError(SAXParseException e) throws SAXException {
            throw new InvalidSpecificationException(e.getMessage());
        }

    }

}
