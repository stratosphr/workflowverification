package specifications.model.visitors;

import codegeneration.sicstus.PlTerm;
import specifications.model.formulas.ConjunctionFormula;
import specifications.model.formulas.DisjunctionFormula;
import specifications.model.formulas.NegationFormula;
import specifications.model.formulas.TransitionFormula;

public class SicstusFormulaVisitor implements IFormulaVisitor {

    @Override
    public PlTerm visit(TransitionFormula transitionFormula) {
        return null;
    }

    @Override
    public PlTerm visit(ConjunctionFormula conjunctionFormula) {
        return null;
    }

    @Override
    public PlTerm visit(DisjunctionFormula disjunctionFormula) {
        System.out.println("Displaying disjunction formula : " + disjunctionFormula);
        return null;
    }

    @Override
    public PlTerm visit(NegationFormula negationFormula) {
        System.out.println("Displaying negation formula : " + negationFormula);
        return null;
    }

    public PlTerm getConstraint() {
        return null;
    }

}
