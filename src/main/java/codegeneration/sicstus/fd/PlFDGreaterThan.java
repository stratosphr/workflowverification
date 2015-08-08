package codegeneration.sicstus.fd;

import codegeneration.sicstus.PlBooleanExpr;
import codegeneration.sicstus.PlTerm;

public class PlFDGreaterThan extends PlBooleanExpr {

    public PlFDGreaterThan(PlTerm left, PlTerm right) {
        super(left, new PlTerm("#>"), right);
    }

}
