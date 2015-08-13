package codegeneration.sicstus.fd;

import codegeneration.sicstus.PlCompoundBooleanExpr;
import codegeneration.sicstus.PlTerm;

public class PLFDEquals extends PlCompoundBooleanExpr {

    public PLFDEquals(PlTerm left, PlTerm right) {
        super(" #= ", left, right);
    }

}
