package exceptions;

public class ShouldNotHappenException extends RuntimeException {

    public ShouldNotHappenException() {
        super("An unexpected exception occured.");
    }

}
