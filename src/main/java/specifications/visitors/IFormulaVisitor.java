package specifications.visitors;

import specifications.model.formulas.ConjunctionFormula;
import specifications.model.formulas.DisjunctionFormula;
import specifications.model.formulas.NegationFormula;
import specifications.model.formulas.TransitionFormula;

public interface IFormulaVisitor {

    void visit(TransitionFormula transitionFormula);

    void visit(ConjunctionFormula conjunctionFormula);

    void visit(DisjunctionFormula disjunctionFormula);

    void visit(NegationFormula negationFormula);

}
