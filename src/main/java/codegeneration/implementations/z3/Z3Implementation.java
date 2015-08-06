package codegeneration.implementations.z3;

import codegeneration.implementations.Implementation;
import petrinets.model.Workflow;
import specifications.model.Specification;

public class Z3Implementation extends Implementation {

    public Z3Implementation(Workflow workflow, Specification specification) {
        super(workflow, specification);
    }

    @Override
    public String getStateEquation() {
        return null;
    }

}
