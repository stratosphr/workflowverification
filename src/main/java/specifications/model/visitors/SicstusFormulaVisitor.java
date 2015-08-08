package specifications.model.visitors;

import codegeneration.sicstus.PlBooleanExpr;
import codegeneration.sicstus.PlCompoundBooleanExpr;
import codegeneration.sicstus.PlTerm;
import codegeneration.sicstus.fd.PlFDConjunction;
import codegeneration.sicstus.fd.PlFDDisjunction;
import codegeneration.sicstus.fd.PlFDGreaterThan;
import codegeneration.sicstus.fd.PlFDNegation;
import specifications.model.formulas.*;
import tools.Prefixes;

public class SicstusFormulaVisitor implements IFormulaVisitor {

    private PlCompoundBooleanExpr constraint;

    public SicstusFormulaVisitor() {
    }

    @Override
    public void visit(TransitionFormula transitionFormula) {
        constraint = new PlFDGreaterThan(new PlTerm(Prefixes.VT + transitionFormula.getName()), new PlTerm(0));
    }

    @Override
    public void visit(ConjunctionFormula conjunctionFormula) {
        constraint = new PlFDConjunction();
        for(IVisitedFormula child : conjunctionFormula.getChildren()){
            SicstusFormulaVisitor sicstusFormulaVisitor = new SicstusFormulaVisitor();
            child.accept(sicstusFormulaVisitor);
            constraint.addChild(sicstusFormulaVisitor.getConstraint());
        }
    }

    @Override
    public void visit(DisjunctionFormula disjunctionFormula) {
        constraint = new PlFDDisjunction();
        for(IVisitedFormula child : disjunctionFormula.getChildren()){
            SicstusFormulaVisitor sicstusFormulaVisitor = new SicstusFormulaVisitor();
            child.accept(sicstusFormulaVisitor);
            constraint.addChild(sicstusFormulaVisitor.getConstraint());
        }
    }

    @Override
    public void visit(NegationFormula negationFormula) {
        constraint = new PlFDNegation();
        for(IVisitedFormula child : negationFormula.getChildren()){
            SicstusFormulaVisitor sicstusFormulaVisitor = new SicstusFormulaVisitor();
            child.accept(sicstusFormulaVisitor);
            constraint.addChild(sicstusFormulaVisitor.getConstraint());
        }
    }

    public PlBooleanExpr getConstraint() {
        return constraint;
    }

}
