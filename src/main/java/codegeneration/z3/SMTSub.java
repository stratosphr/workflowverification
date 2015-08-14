package codegeneration.z3;

import java.util.ArrayList;
import java.util.Arrays;

public class SMTSub extends SMTPredicateCall {

    public SMTSub(SMTTerm... parameters) {
        this(new ArrayList<>(Arrays.asList(parameters)));
    }

    public SMTSub(ArrayList<SMTTerm> parameters) {
        super("-", parameters);
    }

}
