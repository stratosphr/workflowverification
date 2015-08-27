package codegeneration.z3;

import java.util.ArrayList;
import java.util.Arrays;

public class SMTDisjunction extends SMTPredicateCall {

    public SMTDisjunction() {
        this(new ArrayList<SMTTerm>());
    }

    public SMTDisjunction(SMTTerm... parameters) {
        this(new ArrayList<>(Arrays.asList(parameters)));
    }

    public SMTDisjunction(ArrayList<SMTTerm> parameters) {
        super("or", parameters);
    }

}
