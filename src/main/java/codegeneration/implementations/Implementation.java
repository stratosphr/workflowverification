package codegeneration.implementations;

import petrinets.model.Workflow;
import specifications.model.Specification;

public abstract class Implementation {

    private final Workflow workflow;
    private final Specification specification;

    public Implementation(Workflow workflow, Specification specification){
        this.workflow = workflow;
        this.specification = specification;
    }

    public abstract String getStateEquation();

    public abstract String getFormulaConstraint();

}
