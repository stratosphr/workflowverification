package codegeneration.z3;

import tools.StringTools;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class SMTQuantifier extends SMTPredicateCall {

    private ArrayList<SMTVar> parameters;
    private ArrayList<SMTPredicateCall> body;

    public SMTQuantifier(String name, ArrayList<SMTVar> parameters, SMTPredicateCall... body) {
        this(name, parameters, new ArrayList<>(Arrays.asList(body)));
    }

    public SMTQuantifier(String name, ArrayList<SMTVar> parameters, ArrayList<SMTPredicateCall> body) {
        super(name);
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public String toString() {
        ArrayList<String> strParameters = new ArrayList<>();
        for(SMTVar parameter : parameters){
            strParameters.add(parameter.toTypedVar());
        }
        return "(" + representation + " \n(" + StringTools.join(strParameters, " ") + ")\n" + new SMTConjunction(new ArrayList<SMTTerm>(body)) + "\n)";
    }

}
