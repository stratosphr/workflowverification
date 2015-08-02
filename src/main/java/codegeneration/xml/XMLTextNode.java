package codegeneration.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLTextNode extends XMLNode {

    private String text;

    public XMLTextNode(String name, String text) {
        super(name);
        this.text = text;
    }

    @Override
    public void addChild(XMLNode child) {
    }

    @Override
    public Element getElement(Document document) {
        Element node = document.createElement(name);
        node.appendChild(document.createTextNode(text));
        return node;
    }

}
