package codegeneration.sicstus;

import tools.StringTools;

import java.util.ArrayList;
import java.util.Arrays;

public class PlPredicateCall extends PlBooleanExpr {

    private final String name;
    private final ArrayList<PlTerm> parameters;

    public PlPredicateCall(String name) {
        this(name, new ArrayList<PlTerm>());
    }

    public PlPredicateCall(String test, PlTerm... parameters) {
        this(test, new ArrayList<PlTerm>(Arrays.asList(parameters)));
    }

    public PlPredicateCall(String name, ArrayList<PlTerm> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        String str = name;
        if (!parameters.isEmpty()) {
            str += "(" + StringTools.join(parameters, ", ") + ")";
        }
        return str;
    }

}
