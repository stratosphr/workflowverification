package codegeneration.sicstus.fd;

import codegeneration.sicstus.PlCompoundBooleanExpr;
import codegeneration.sicstus.PlTerm;

public class PlFDConjunction extends PlCompoundBooleanExpr {

    public PlFDConjunction(PlTerm... children) {
        super(" #/\\ ", children);
    }

}
