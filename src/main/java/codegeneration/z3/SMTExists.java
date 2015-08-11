package codegeneration.z3;

import java.util.ArrayList;

public class SMTExists extends SMTQuantifier {

    public SMTExists(ArrayList<SMTVar> parameters, SMTPredicateCall body) {
        super("exists", parameters, body);
    }

}
