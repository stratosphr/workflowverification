package exceptions;

public class NoSourceFoundOnWorkflowException extends RuntimeException {

    public NoSourceFoundOnWorkflowException(){
        super("No source place was found on workflow.");
    }

}
