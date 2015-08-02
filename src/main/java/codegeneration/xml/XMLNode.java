package codegeneration.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;

public class XMLNode {

    protected String name;
    private Element element;
    private ArrayList<XMLNode> children;
    private HashMap<String, String> attributes;

    public XMLNode(String name) {
        this.name = name;
        children = new ArrayList<>();
        attributes = new HashMap<>();
    }

    public void addChild(XMLNode child) {
        children.add(child);
    }

    public void addAttribute(String name, String value) {
        attributes.put(name, value);
    }

    public Element getElement(Document document) {
        if(element == null){
            element = document.createElement(name);
            for(String attribute : attributes.keySet()){
                element.setAttribute(attribute, attributes.get(attribute));
            }
            for(XMLNode node : children){
                element.appendChild(node.getElement(document));
            }
        }
        return element;
    }

}
