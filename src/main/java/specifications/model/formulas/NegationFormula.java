package specifications.model.formulas;

import specifications.visitors.IFormulaVisitor;

public class NegationFormula extends CompoundFormula {

    public NegationFormula(Formula child) {
        super("not", child);
    }

    @Override
    public void accept(IFormulaVisitor formulaVisitor) {
        formulaVisitor.visit(this);
    }

}
