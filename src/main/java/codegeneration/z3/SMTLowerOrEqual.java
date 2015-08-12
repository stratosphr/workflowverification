package codegeneration.z3;

public class SMTLowerOrEqual extends SMTPredicateCall{

    public SMTLowerOrEqual(SMTTerm left, SMTTerm right) {
        super("<=", left, right);
    }

}
