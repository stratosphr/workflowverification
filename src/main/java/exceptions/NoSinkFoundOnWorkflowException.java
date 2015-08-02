package exceptions;

public class NoSinkFoundOnWorkflowException extends RuntimeException {

    public NoSinkFoundOnWorkflowException(){
        super("No sink place was found on workflow.");
    }

}
