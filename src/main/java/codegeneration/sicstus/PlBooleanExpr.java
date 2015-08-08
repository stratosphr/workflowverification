package codegeneration.sicstus;

import tools.StringTools;

public class PlBooleanExpr extends PlTerm {

    public PlBooleanExpr() {

    }

    public PlBooleanExpr(PlTerm... terms) {
        super(StringTools.join(terms, " "));
    }

}
