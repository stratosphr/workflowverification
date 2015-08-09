package codegeneration.implementations.sicstus;

import petrinets.model.Workflow;
import specifications.model.Specification;

public class OneTransitionPerSegmentSicstusImplementation extends SicstusImplementation {

    public OneTransitionPerSegmentSicstusImplementation(Workflow workflow, Specification specification) {
        super(workflow, specification);
    }

}
