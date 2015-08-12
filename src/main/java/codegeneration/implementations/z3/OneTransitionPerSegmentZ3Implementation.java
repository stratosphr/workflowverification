package codegeneration.implementations.z3;

import codegeneration.z3.SMTPredicateDefinition;
import petrinets.model.Workflow;
import specifications.model.Specification;

public class OneTransitionPerSegmentZ3Implementation extends Z3Implementation {

    public OneTransitionPerSegmentZ3Implementation(Workflow workflow, Specification specification) {
        super(workflow, specification);
    }

    @Override
    public SMTPredicateDefinition getStateEquation() {
        return null;
    }

}
