package codegeneration.sicstus;

import tools.StringTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlPredicateDefinition {

    private final String name;
    private final List<PlTerm> parameters;
    private final List<PlBooleanExpr> body;

    public PlPredicateDefinition(String name) {
        this(name, new ArrayList<PlTerm>(), new ArrayList<PlBooleanExpr>());
    }

    public PlPredicateDefinition(String name, PlTerm... parameters) {
        this(name, new ArrayList<>(Arrays.asList(parameters)), new ArrayList<PlBooleanExpr>());
    }

    public PlPredicateDefinition(String name, ArrayList<PlTerm> parameters) {
        this(name, parameters, new ArrayList<PlBooleanExpr>());
    }

    public PlPredicateDefinition(String name, PlTerm[] parameters, PlBooleanExpr[] body) {
        this(name, new ArrayList<>(Arrays.asList(parameters)), new ArrayList<>(Arrays.asList(body)));
    }

    public PlPredicateDefinition(String name, ArrayList<PlTerm> parameters, ArrayList<PlBooleanExpr> body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public String toString() {
        String str = name;
        if (!parameters.isEmpty()) {
            str += "(" + StringTools.join(parameters, ", ") + ")";
        }
        if (!body.isEmpty()) {
            str += ":-\n";
            str += "\t" + StringTools.join(body, ",\n\t");
        }
        str += ".";
        return str;
    }

    public PlPredicateCall getCallWith(PlTerm... parameters) {
        return getCallWith(new ArrayList<>(Arrays.asList(parameters)));
    }

    public PlPredicateCall getCallWith(List<PlTerm> parameters) {
        return new PlPredicateCall(
                name,
                parameters
        );
    }

}
