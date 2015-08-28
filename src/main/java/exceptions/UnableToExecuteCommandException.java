package exceptions;

public class UnableToExecuteCommandException extends RuntimeException {

    public UnableToExecuteCommandException(String message) {
        super("Unable to execute command : \n" + message);
    }

}
