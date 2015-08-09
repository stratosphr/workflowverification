package codegeneration.implementations.z3;

import petrinets.model.Workflow;
import specifications.model.Specification;

public class OneTransitionPerSegmentZ3Implementation extends Z3Implementation {

    public OneTransitionPerSegmentZ3Implementation(Workflow workflow, Specification specification) {
        super(workflow, specification);
    }

    @Override
    public String getStateEquation() {
        return null;
    }

    @Override
    public Object getNoSiphon() {
        return null;
    }

}
