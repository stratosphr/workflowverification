package codegeneration.z3;

import codegeneration.z3.SMTPredicateCall;
import codegeneration.z3.SMTTerm;
import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class SMTEquality extends SMTPredicateCall {

    public SMTEquality(@NotNull SMTTerm... terms) {
        this(new ArrayList<>(Arrays.asList(terms)));
    }

    public SMTEquality(@NotNull ArrayList<SMTTerm> terms) {
        super("=", terms);
    }

}
