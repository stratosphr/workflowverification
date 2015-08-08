package codegeneration.sicstus;

public class PlDisjunction extends PlCompoundBooleanExpr {

    public PlDisjunction(PlTerm... children) {
        super("; ", children);
    }

}
