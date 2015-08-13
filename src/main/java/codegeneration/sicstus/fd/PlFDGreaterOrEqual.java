package codegeneration.sicstus.fd;

import codegeneration.sicstus.PlCompoundBooleanExpr;
import codegeneration.sicstus.PlTerm;

public class PlFDGreaterOrEqual extends PlCompoundBooleanExpr {

    public PlFDGreaterOrEqual(PlTerm left, PlTerm right) {
        super(" #>= ", left, right);
    }

}
