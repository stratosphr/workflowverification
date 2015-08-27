package codegeneration.z3;

import java.util.ArrayList;
import java.util.Arrays;

public class SMTConjunction extends SMTPredicateCall {

    public SMTConjunction() {
        this(new ArrayList<SMTTerm>());
    }

    public SMTConjunction(SMTTerm... parameters) {
        this(new ArrayList<>(Arrays.asList(parameters)));
    }

    public SMTConjunction(ArrayList<SMTTerm> parameters) {
        super("and", parameters);
    }

}
