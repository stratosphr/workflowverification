package exceptions;

public class InvalidWorkflowException extends RuntimeException {

    public InvalidWorkflowException(String message) {
        super("Le fichier pnml représentant le workflow est invalide vis-à-vis du DTD \"pnml.dtd\" :\n" + message);
    }

}
