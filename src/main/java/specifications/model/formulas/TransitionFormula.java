package specifications.model.formulas;

import codegeneration.sicstus.PlBooleanExpr;
import petrinets.model.Transition;
import petrinets.model.Workflow;
import specifications.model.visitors.IFormulaVisitor;

import java.util.LinkedHashSet;

public class TransitionFormula extends Formula {

    public TransitionFormula(String name) {
        super(name);
    }

    @Override
    public LinkedHashSet<Transition> getUsedTransitions(Workflow workflow) {
        LinkedHashSet<Transition> usedTransitions = new LinkedHashSet<>();
        if (workflow.getTransitions().contains(new Transition(getName()))) {
            usedTransitions.add(new Transition(getName()));
        }
        return usedTransitions;
    }

    public String getName() {
        return name;
    }

    @Override
    public PlBooleanExpr accept(IFormulaVisitor formulaVisitor) {
        formulaVisitor.visit(this);
        return null;
    }

}
