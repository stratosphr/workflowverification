package codegeneration.sicstus;

public class PlConjunction extends PlCompoundBooleanExpr {

    public PlConjunction(PlTerm... children) {
        super(", ", children);
    }

}
