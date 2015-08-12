package codegeneration.z3;

public class SMTImplication extends SMTPredicateCall {

    public SMTImplication(SMTTerm left, SMTTerm right) {
        super("=>", left, right);
    }

}
