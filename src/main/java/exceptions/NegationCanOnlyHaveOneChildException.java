package exceptions;

public class NegationCanOnlyHaveOneChildException extends RuntimeException {

    public NegationCanOnlyHaveOneChildException(){
        super("Attempt to add a child to a negation that already contains one.");
    }

}
