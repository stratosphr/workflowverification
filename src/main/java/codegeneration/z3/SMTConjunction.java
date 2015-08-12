package codegeneration.z3;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class SMTConjunction extends SMTPredicateCall {

    public SMTConjunction() {
        this(new ArrayList<SMTTerm>());
    }

    public SMTConjunction(@NotNull SMTTerm... parameters) {
        this(new ArrayList<>(Arrays.asList(parameters)));
    }

    public SMTConjunction(@NotNull ArrayList<SMTTerm> parameters) {
        super("and", parameters);
    }

}
