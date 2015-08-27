package codegeneration.implementations.sicstus;

import mvc.models.ParametersModel;
import petrinets.model.Workflow;
import specifications.model.Specification;

public class OneTransitionPerSegmentSicstusImplementation extends SicstusImplementation {

    public OneTransitionPerSegmentSicstusImplementation(Workflow workflow, Specification specification, ParametersModel parametersModel) {
        super(workflow, specification, parametersModel);
    }

}
