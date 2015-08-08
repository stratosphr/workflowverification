package specifications.model.formulas;

import petrinets.model.Transition;
import petrinets.model.Workflow;
import specifications.visitors.IVisitedFormula;

import java.util.LinkedHashSet;

public abstract class Formula implements IVisitedFormula {

    protected String name;

    public Formula(String name) {
        this.name = name;
    }

    public abstract LinkedHashSet<Transition> getUsedTransitions(Workflow workflow);

    @Override
    public String toString() {
        return name;
    }

}
