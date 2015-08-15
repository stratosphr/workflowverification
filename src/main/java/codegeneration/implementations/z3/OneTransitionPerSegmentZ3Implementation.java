package codegeneration.implementations.z3;

import codegeneration.z3.SMTPredicateDefinition;
import mvc.model.ParametersModel;
import petrinets.model.Workflow;
import specifications.model.Specification;

public class OneTransitionPerSegmentZ3Implementation extends Z3Implementation {

    public OneTransitionPerSegmentZ3Implementation(Workflow workflow, Specification specification, ParametersModel parametersModel) {
        super(workflow, specification, parametersModel);
    }

    @Override
    public SMTPredicateDefinition getStateEquation() {
        return null;
    }

}
