package codegeneration.sicstus;

import tools.StringTools;

public class PlConjunction extends PlBooleanExpr {

    private final PlTerm[] children;

    public PlConjunction(PlTerm... children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "(" + StringTools.join(children, ", ") + ")";
    }

}
