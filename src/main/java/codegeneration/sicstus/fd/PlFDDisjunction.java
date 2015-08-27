package codegeneration.sicstus.fd;

import codegeneration.sicstus.PlCompoundBooleanExpr;
import codegeneration.sicstus.PlTerm;

public class PlFDDisjunction extends PlCompoundBooleanExpr {

    public PlFDDisjunction(PlTerm... children) {
        super(" #\\/ ", children);
    }

}
