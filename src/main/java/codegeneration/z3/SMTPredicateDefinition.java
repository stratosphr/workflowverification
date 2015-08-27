package codegeneration.z3;

import tools.StringTools;

import java.util.ArrayList;
import java.util.Arrays;

public class SMTPredicateDefinition {

    private String name;
    private ArrayList<SMTVar> parameters;
    private ESMTType type;
    private ArrayList<SMTTerm> body;

    public SMTPredicateDefinition(String name, SMTVar[] parameters, ESMTType type) {
        this(name, new ArrayList<>(Arrays.asList(parameters)), type);
    }

    public SMTPredicateDefinition(String name, ArrayList<SMTVar> parameters, ESMTType type) {
        this(name, parameters, type, new ArrayList<SMTTerm>());
    }

    public SMTPredicateDefinition(String name, SMTVar[] parameters, ESMTType type, SMTTerm... body) {
        this(name, new ArrayList<>(Arrays.asList(parameters)), type, new ArrayList<>(Arrays.asList(body)));
    }

    public SMTPredicateDefinition(String name, ArrayList<SMTVar> parameters, ESMTType type, SMTTerm... body) {
        this(name, parameters, type, new ArrayList<>(Arrays.asList(body)));
    }

    public SMTPredicateDefinition(String name, ArrayList<SMTVar> parameters, ESMTType type, ArrayList<SMTTerm> body) {
        this.name = name;
        this.parameters = parameters;
        this.type = type;
        this.body = body;
    }

    public SMTPredicateCall getCallWith(SMTTerm... parameters) {
        return getCallWith(new ArrayList<>(Arrays.asList(parameters)));
    }

    public SMTPredicateCall getCallWith(ArrayList<SMTTerm> parameters) {
        return new SMTPredicateCall(
                name,
                parameters
        );
    }

    @Override
    public String toString() {
        ArrayList<String> strParameters = new ArrayList<>();
        for(SMTVar parameter : parameters){
            strParameters.add(parameter.toTypedVar());
        }
        return "(define-fun " + name + " \n(" + StringTools.join(strParameters, " ") + ")\n" + type + "\n" + new SMTConjunction(body) + "\n)";
    }

}
