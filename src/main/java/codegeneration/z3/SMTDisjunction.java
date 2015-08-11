package codegeneration.z3;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;

public class SMTDisjunction extends SMTPredicateCall {

    public SMTDisjunction(){
        this(new ArrayList<SMTTerm>());
    }

    public SMTDisjunction(@NotNull ArrayList<SMTTerm> parameters) {
        super("or", parameters);
    }

}
