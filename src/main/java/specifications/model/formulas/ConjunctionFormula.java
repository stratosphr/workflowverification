package specifications.model.formulas;

import codegeneration.sicstus.PlBooleanExpr;
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
    public PlBooleanExpr accept(IFormulaVisitor formulaVisitor) {
        formulaVisitor.visit(this);
        super.accept(formulaVisitor);
        return null;
    }

}
