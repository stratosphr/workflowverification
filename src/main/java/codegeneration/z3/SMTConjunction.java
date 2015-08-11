package codegeneration.z3;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;

public class SMTConjunction extends SMTPredicateCall {

    public SMTConjunction() {
        this(new ArrayList<SMTTerm>());
    }

    public SMTConjunction(@NotNull ArrayList<SMTTerm> parameters) {
        super("and", parameters);
    }

}
