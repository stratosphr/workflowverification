package codegeneration.z3;

import java.util.ArrayList;

public class SMTSum extends SMTPredicateCall {

    public SMTSum(ArrayList<? extends SMTTerm> parameters) {
        super("+", parameters);
    }

}
