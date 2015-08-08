package codegeneration.sicstus.fd;

import codegeneration.sicstus.PlCompoundBooleanExpr;
import codegeneration.sicstus.PlTerm;
import com.sun.istack.internal.NotNull;

public class PlFDConjunction extends PlCompoundBooleanExpr {

    public PlFDConjunction(@NotNull PlTerm... children) {
        super(" #/\\ ", children);
    }

}
