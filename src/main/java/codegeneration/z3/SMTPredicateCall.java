package codegeneration.z3;

import tools.StringTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SMTPredicateCall extends SMTTerm {

    protected List<SMTTerm> parameters;

    public SMTPredicateCall(String name, SMTTerm... parameters) {
        this(name, new ArrayList<>(Arrays.asList(parameters)));
    }

    public SMTPredicateCall(String name, ArrayList<SMTTerm> parameters) {
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
