package specifications.model.formulas;

import specifications.model.visitors.IFormulaVisitor;

import java.util.ArrayList;

public class ConjunctionFormula extends CompoundFormula {

    public ConjunctionFormula(ArrayList<Formula> children) {
        super("and", children);
    }

    public ConjunctionFormula(Formula... children) {
        super("and", children);
    }

    @Override
    public void accept(IFormulaVisitor formulaVisitor) {
        formulaVisitor.visit(this);
    }

}
