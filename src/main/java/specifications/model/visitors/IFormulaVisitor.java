package specifications.model.visitors;

import codegeneration.sicstus.PlTerm;
import specifications.model.formulas.ConjunctionFormula;
import specifications.model.formulas.DisjunctionFormula;
import specifications.model.formulas.NegationFormula;
import specifications.model.formulas.TransitionFormula;

public interface IFormulaVisitor {

    PlTerm visit(TransitionFormula transitionFormula);

    PlTerm visit(ConjunctionFormula conjunctionFormula);

    PlTerm visit(DisjunctionFormula disjunctionFormula);

    PlTerm visit(NegationFormula negationFormula);

}
