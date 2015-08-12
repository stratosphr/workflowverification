package codegeneration.z3;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class SMTDisjunction extends SMTPredicateCall {

    public SMTDisjunction() {
        this(new ArrayList<SMTTerm>());
    }

    public SMTDisjunction(@NotNull SMTTerm... parameters) {
        this(new ArrayList<>(Arrays.asList(parameters)));
    }

    public SMTDisjunction(@NotNull ArrayList<SMTTerm> parameters) {
        super("or", parameters);
    }

}
