package exceptions;

public class XMLParserConfigurationException extends RuntimeException{

    public XMLParserConfigurationException(String message) {
        super("Unable to create XML Document :\n" + message);
    }

}
