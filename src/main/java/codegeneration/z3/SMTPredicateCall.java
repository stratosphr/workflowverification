package codegeneration.z3;

import com.sun.istack.internal.NotNull;
import tools.StringTools;

import java.util.ArrayList;
import java.util.Arrays;

public class SMTPredicateCall extends SMTTerm {

    protected ArrayList<SMTTerm> parameters;

    public SMTPredicateCall(String name, @NotNull SMTTerm... parameters) {
        this(name, new ArrayList<>(Arrays.asList(parameters)));
    }

    public SMTPredicateCall(String name, @NotNull ArrayList<SMTTerm> parameters) {
        super(name);
        this.parameters = parameters;
    }

    public void addParameter(SMTTerm parameter) {
        parameters.add(parameter);
    }

    @Override
    public String toString() {
        if (parameters.isEmpty()) {
            return "(" + representation + ")";
        } else {
            return "(" + representation + " " + StringTools.join(parameters, " ") + ")";
        }
    }

}
