package codegeneration.sicstus.fd;

import codegeneration.sicstus.PlList;
import codegeneration.sicstus.PlPredicateCall;
import codegeneration.sicstus.PlTerm;

public class PlFDLabeling extends PlPredicateCall {

    public PlFDLabeling(PlTerm vars) {
        this(new PlList(), vars);
    }

    public PlFDLabeling(PlList heuristics, PlTerm vars) {
        super("labeling", heuristics, vars);
    }

}
