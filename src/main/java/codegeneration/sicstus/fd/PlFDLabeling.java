package codegeneration.sicstus.fd;

import codegeneration.sicstus.PlList;
import codegeneration.sicstus.PlPredicateCall;

public class PlFDLabeling extends PlPredicateCall {

    public PlFDLabeling(PlList vars) {
        this(new PlList(), vars);
    }

    public PlFDLabeling(PlList heuristics, PlList vars) {
        super("labeling", heuristics, vars);
    }

}
