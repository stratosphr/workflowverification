package specifications.visitors;

import codegeneration.z3.*;
import specifications.model.formulas.ConjunctionFormula;
import specifications.model.formulas.DisjunctionFormula;
import specifications.model.formulas.NegationFormula;
import specifications.model.formulas.TransitionFormula;
import tools.Prefixes;

public class Z3FormulaVisitor implements IFormulaVisitor {

    private SMTPredicateCall constraint;

    public Z3FormulaVisitor() {
    }


    @Override
    public void visit(TransitionFormula transitionFormula) {
        constraint = new SMTGreaterThan(new SMTTerm(Prefixes.VT + transitionFormula.getName()), new SMTTerm(0));
    }

    @Override
    public void visit(ConjunctionFormula conjunctionFormula) {
        constraint = new SMTConjunction();
        for (IVisitedFormula child : conjunctionFormula.getChildren()) {
            Z3FormulaVisitor z3FormulaVisitor = new Z3FormulaVisitor();
            child.accept(z3FormulaVisitor);
            constraint.addParameter(z3FormulaVisitor.getConstraint());
        }
    }

    @Override
    public void visit(DisjunctionFormula disjunctionFormula) {
        constraint = new SMTDisjunction();
        for (IVisitedFormula child : disjunctionFormula.getChildren()) {
            Z3FormulaVisitor z3FormulaVisitor = new Z3FormulaVisitor();
            child.accept(z3FormulaVisitor);
            constraint.addParameter(z3FormulaVisitor.getConstraint());
        }
    }

    @Override
    public void visit(NegationFormula negationFormula) {
        constraint = new SMTNegation();
        for (IVisitedFormula child : negationFormula.getChildren()) {
            Z3FormulaVisitor z3FormulaVisitor = new Z3FormulaVisitor();
            child.accept(z3FormulaVisitor);
            constraint.addParameter(z3FormulaVisitor.getConstraint());
        }
    }

    public SMTPredicateCall getConstraint() {
        return constraint;
    }

}
