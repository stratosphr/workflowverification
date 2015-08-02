package exceptions;

public class InvalidSicstusQueryException extends RuntimeException {

    public InvalidSicstusQueryException(String message) {
        super("The sicstus query you entered is invalid.\n" + message);
    }

}
