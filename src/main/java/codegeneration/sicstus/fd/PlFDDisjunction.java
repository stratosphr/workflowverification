package codegeneration.sicstus.fd;

import codegeneration.sicstus.PlCompoundBooleanExpr;
import codegeneration.sicstus.PlTerm;
import com.sun.istack.internal.NotNull;

public class PlFDDisjunction extends PlCompoundBooleanExpr {

    public PlFDDisjunction(@NotNull PlTerm... children) {
        super(" #\\/ ", children);
    }

}
