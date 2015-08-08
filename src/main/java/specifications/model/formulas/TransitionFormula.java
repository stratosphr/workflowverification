package specifications.model.formulas;

import petrinets.model.Transition;
import petrinets.model.Workflow;
import specifications.visitors.IFormulaVisitor;

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
    public void accept(IFormulaVisitor formulaVisitor) {
        formulaVisitor.visit(this);
    }

}
