package codegeneration.z3;

public class SMTDomain extends SMTConjunction {

    public SMTDomain(SMTTerm term, SMTTerm inf, SMTTerm sup) {
        super(new SMTGreaterOrEqual(
                term,
                inf
        ), new SMTLowerOrEqual(
                term,
                sup
        ));
    }

}
