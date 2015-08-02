package codegeneration.sicstus;

import tools.StringTools;

public class PlDisjunction extends PlBooleanExpr {

    private final PlTerm[] children;

    public PlDisjunction(PlTerm... children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "(" + StringTools.join(children, "; ") + ")";
    }

}
