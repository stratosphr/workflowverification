package exceptions;

public class UnknownSicstusImplementationException extends RuntimeException {

    public UnknownSicstusImplementationException(String implementationName) {
        super("The \"" + implementationName + "\" implementation is unknown.");
    }

}
