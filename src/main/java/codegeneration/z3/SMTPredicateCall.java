package codegeneration.z3;

import com.sun.istack.internal.NotNull;
import tools.StringTools;

import java.util.ArrayList;
import java.util.Arrays;

public class SMTPredicateCall extends SMTTerm {

    private String name;
    private ArrayList<? extends SMTTerm> parameters;

    public SMTPredicateCall(String name, @NotNull SMTTerm... parameters) {
        this(name, new ArrayList<>(Arrays.asList(parameters)));
    }

    public SMTPredicateCall(String name, @NotNull ArrayList<? extends SMTTerm> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "(" + name + " " + StringTools.join(parameters, " ") + ")";
    }

}
