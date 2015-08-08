package codegeneration.sicstus.fd;

import codegeneration.sicstus.PlCompoundBooleanExpr;
import codegeneration.sicstus.PlTerm;

public class PlFDGreaterThan extends PlCompoundBooleanExpr {

    public PlFDGreaterThan(PlTerm left, PlTerm right) {
        super(left, new PlTerm("#>"), right);
    }

}
