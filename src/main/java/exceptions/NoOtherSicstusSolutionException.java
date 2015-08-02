package exceptions;

public class NoOtherSicstusSolutionException extends RuntimeException {

    public NoOtherSicstusSolutionException() {
        super("No other sicstus solution was found. Please call method \"hasSolution()\" before \"getSolution()\".");
    }

}
