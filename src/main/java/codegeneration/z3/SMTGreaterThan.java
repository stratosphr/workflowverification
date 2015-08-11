package codegeneration.z3;

public class SMTGreaterThan extends SMTPredicateCall {

    public SMTGreaterThan(SMTTerm left, SMTTerm right) {
        super(">", left, right);
    }

}
