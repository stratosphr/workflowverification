package codegeneration.implementations;

import petrinets.model.Workflow;
import specifications.model.Specification;

public abstract class Implementation {

    protected final Workflow workflow;
    protected final Specification specification;

    public Implementation(Workflow workflow, Specification specification) {
        this.workflow = workflow;
        this.specification = specification;
        init();
    }

    public abstract void init();

    public abstract String getStateEquation();

    public abstract String getFormulaConstraint();

    public abstract String getOverApproximation1();

    public abstract String getOverApproximation1Assertion();

}
