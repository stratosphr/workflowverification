package specifications;

import exceptions.InvalidWorkflowException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import specifications.formulas.*;

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
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e.getMessage());
        } catch (SAXException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static Formula getFormula(Node child) {
        String elementName = child.getNodeName();
        if (elementName.equals("transition")) {
            String transitionName = child.getAttributes().getNamedItem("name").getNodeValue();
            return new TransitionFormula(transitionName);
        } else if (elementName.equals("conjunction")) {
            NodeList childrenNodes = child.getChildNodes();
            ArrayList<Formula> children = new ArrayList<Formula>();
            for (int i = 0; i < childrenNodes.getLength(); i++) {
                Node childNode = childrenNodes.item(i);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    children.add(getFormula(childNode));
                }
            }
            return new ConjunctionFormula(children);
        } else if (elementName.equals("disjunction")) {
            NodeList childrenNodes = child.getChildNodes();
            ArrayList<Formula> children = new ArrayList<Formula>();
            for (int i = 0; i < childrenNodes.getLength(); i++) {
                Node childNode = childrenNodes.item(i);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    children.add(getFormula(childNode));
                }
            }
            return new DisjunctionFormula(children);
        } else if (elementName.equals("negation")) {
            NodeList childrenNodes = child.getChildNodes();
            for (int i = 0; i < childrenNodes.getLength(); i++) {
                Node childNode = childrenNodes.item(i);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    return new NegationFormula(getFormula(childNode));
                }
            }
        } else {
            throw new InvalidWorkflowException("Should never happen.");
        }
        return null;
    }

    private static class SpecificationParserErrorHandler implements ErrorHandler {

        public void warning(SAXParseException e) throws SAXException {
            throw new InvalidWorkflowException(e.getMessage());
        }

        public void error(SAXParseException e) throws SAXException {
            throw new InvalidWorkflowException(e.getMessage());
        }

        public void fatalError(SAXParseException e) throws SAXException {
            throw new InvalidWorkflowException(e.getMessage());
        }

    }

}
