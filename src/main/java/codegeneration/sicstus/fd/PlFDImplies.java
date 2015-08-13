package codegeneration.sicstus.fd;

import codegeneration.sicstus.PlCompoundBooleanExpr;
import codegeneration.sicstus.PlTerm;

public class PlFDImplies extends PlCompoundBooleanExpr {

    public PlFDImplies(PlTerm left, PlTerm right) {
        super(" #=> ", left, right);
    }

}
