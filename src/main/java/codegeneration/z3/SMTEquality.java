package codegeneration.z3;

import java.util.ArrayList;
import java.util.Arrays;

public class SMTEquality extends SMTPredicateCall {

    public SMTEquality(SMTTerm... terms) {
        this(new ArrayList<>(Arrays.asList(terms)));
    }

    public SMTEquality(ArrayList<SMTTerm> terms) {
        super("=", terms);
    }

}
