package codegeneration.sicstus;

public class PlSub extends PlCompoundBooleanExpr {

    public PlSub(PlTerm... terms) {
        super(" - ", terms);
    }

}
