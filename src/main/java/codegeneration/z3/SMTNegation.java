package codegeneration.z3;

import exceptions.NegationCanOnlyHaveOneChildException;

import java.util.ArrayList;
import java.util.Arrays;

public class SMTNegation extends SMTPredicateCall {

    public SMTNegation() {
        this(new ArrayList<SMTTerm>());
    }

    public SMTNegation(SMTTerm parameter) {
        this(new ArrayList<>(Arrays.asList(new SMTTerm[]{parameter})));
    }

    public SMTNegation(ArrayList<SMTTerm> parameters) {
        super("not", parameters);
    }

    @Override
    public void addParameter(SMTTerm parameter) {
        if (parameters.size() > 0) {
            throw new NegationCanOnlyHaveOneChildException();
        } else {
            super.addParameter(parameter);
        }
    }

}
