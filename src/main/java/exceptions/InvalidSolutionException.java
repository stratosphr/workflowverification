package exceptions;

import java.util.ArrayList;

public class InvalidSolutionException extends Exception {

    public InvalidSolutionException(ArrayList<String> solution) {
        super("The z3 solution is invalid : " + solution.toString() + ".\nThis is probably due to a bug (either from z3 or this software).");
    }

}
