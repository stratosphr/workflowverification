package exceptions;

public class UnknownZ3ImplementationException extends RuntimeException {

    public UnknownZ3ImplementationException(String implementationName) {
        super("The \"" + implementationName + "\" implementation is unknown.");
    }

}
