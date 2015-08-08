package specifications.model.formulas;

import codegeneration.sicstus.PlBooleanExpr;
import specifications.model.visitors.IFormulaVisitor;

public class NegationFormula extends CompoundFormula {

    public NegationFormula(Formula child) {
        super("not", child);
    }

    @Override
    public PlBooleanExpr accept(IFormulaVisitor formulaVisitor) {
        formulaVisitor.visit(this);
        super.accept(formulaVisitor);
        return null;
    }

}
