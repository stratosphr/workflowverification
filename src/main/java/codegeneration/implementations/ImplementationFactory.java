package codegeneration.implementations;

import codegeneration.implementations.sicstus.SicstusImplementation;
import codegeneration.implementations.sicstus.ESicstusImplementation;
import codegeneration.implementations.sicstus.OneTransitionPerSegmentSicstusImplementation;
import codegeneration.implementations.z3.Z3Implementation;
import codegeneration.implementations.z3.EZ3Implementation;
import codegeneration.implementations.z3.OneTransitionPerSegmentZ3Implementation;
import exceptions.UnknownSicstusImplementationException;
import exceptions.UnknownZ3ImplementationException;
import petrinets.model.Workflow;
import specifications.model.Specification;

public class ImplementationFactory {

    private ImplementationFactory() {

    }

    public static SicstusImplementation getImplementation(ESicstusImplementation sicstusImplementation, Workflow workflow, Specification specification) {
        switch (sicstusImplementation) {
            case DEFAULT:
                return new SicstusImplementation(workflow, specification);
            case ONE_TRANSITION_PER_SEGMENT:
                return new OneTransitionPerSegmentSicstusImplementation(workflow, specification);
            default:
                throw new UnknownSicstusImplementationException(sicstusImplementation.toString());
        }
    }

    public static Z3Implementation getImplementation(EZ3Implementation z3Implementation, Workflow workflow, Specification specification) {
        switch (z3Implementation) {
            case DEFAULT:
                return new Z3Implementation(workflow, specification);
            case ONE_TRANSITION_PER_SEGMENT:
                return new OneTransitionPerSegmentZ3Implementation(workflow, specification);
            default:
                throw new UnknownZ3ImplementationException(z3Implementation.toString());
        }
    }

}
