package exceptions;

import se.sics.jasper.Term;

public class UnparsablePlTermException extends RuntimeException {

    public UnparsablePlTermException(Term term) {
        super("The term \"" + term + "\" can't be parsed to a PlTerm.");
    }

}
