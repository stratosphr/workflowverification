package codegeneration.sicstus;

import tools.StringTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlPredicateCall extends PlBooleanExpr {

    private final String name;
    private final List<PlTerm> parameters;

    public PlPredicateCall(String name) {
        this(name, new ArrayList<PlTerm>());
    }

    public PlPredicateCall(String name, PlTerm... parameters) {
        this(name, new ArrayList<>(Arrays.asList(parameters)));
    }

    public PlPredicateCall(String name, List<PlTerm> parameters) {
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
