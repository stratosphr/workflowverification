package codegeneration.sicstus.fd;

import codegeneration.sicstus.PlCompoundBooleanExpr;
import codegeneration.sicstus.PlTerm;

public class PlFDLowerOrEqual extends PlCompoundBooleanExpr {

    public PlFDLowerOrEqual(PlTerm left, PlTerm right) {
        super(" #=< ", left, right);
    }

}
