package exceptions;

public class UnableToInitializeSicstusException extends RuntimeException {

    public UnableToInitializeSicstusException(String message) {
        super("Initialization of sicstus failed.\n" + message);
    }

}
