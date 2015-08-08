package specifications.model.formulas;

import specifications.visitors.IFormulaVisitor;

import java.util.ArrayList;

public class DisjunctionFormula extends CompoundFormula {

    public DisjunctionFormula(ArrayList<Formula> children) {
        super("or", children);
    }

    public DisjunctionFormula(Formula... children) {
        super("or", children);
    }

    @Override
    public void accept(IFormulaVisitor formulaVisitor) {
        formulaVisitor.visit(this);
    }

}
