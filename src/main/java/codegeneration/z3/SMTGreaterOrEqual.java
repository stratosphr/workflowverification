package codegeneration.z3;

public class SMTGreaterOrEqual extends SMTPredicateCall {

    public SMTGreaterOrEqual(SMTTerm left, SMTTerm right) {
        super(">=", left, right);
    }

}
