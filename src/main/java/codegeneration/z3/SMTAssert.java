package codegeneration.z3;

public class SMTAssert extends SMTPredicateCall {

    public SMTAssert(SMTPredicateCall assertion) {
        super("assert", assertion);
    }

}
