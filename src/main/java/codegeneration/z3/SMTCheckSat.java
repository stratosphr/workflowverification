package codegeneration.z3;

import java.util.List;

public class SMTCheckSat extends SMTPredicateCall {

    public SMTCheckSat() {
        this(null);
    }

    public SMTCheckSat(List<SMTTerm> z3Heuristics) {
        super("check-sat" + (z3Heuristics == null || z3Heuristics.isEmpty() ? "" : "-using"), z3Heuristics);
    }

}
