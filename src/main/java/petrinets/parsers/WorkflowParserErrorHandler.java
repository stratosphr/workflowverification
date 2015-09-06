package petrinets.parsers;


import exceptions.InvalidWorkflowException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class WorkflowParserErrorHandler implements ErrorHandler {

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

