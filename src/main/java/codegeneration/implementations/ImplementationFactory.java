package codegeneration.implementations;

import codegeneration.implementations.sicstus.SicstusImplementation;
import codegeneration.implementations.sicstus.ESicstusImplementations;
import codegeneration.implementations.sicstus.OneTransitionPerSegmentSicstusImplementation;
import codegeneration.implementations.z3.Z3Implementation;
import codegeneration.implementations.z3.EZ3Implementations;
import codegeneration.implementations.z3.OneTransitionPerSegmentZ3Implementation;
import exceptions.UnknownSicstusImplementationException;
import exceptions.UnknownZ3ImplementationException;
import mvc.models.ParametersModel;
import petrinets.model.Workflow;
import specifications.model.Specification;

public class ImplementationFactory {

    private ImplementationFactory() {

    }

    public static SicstusImplementation getImplementation(ESicstusImplementations sicstusImplementation, Workflow workflow, Specification specification, ParametersModel parametersModel) {
        switch (sicstusImplementation) {
            case DEFAULT:
                return new SicstusImplementation(workflow, specification, parametersModel);
            case ONE_TRANSITION_PER_SEGMENT:
                return new OneTransitionPerSegmentSicstusImplementation(workflow, specification, parametersModel);
            default:
                throw new UnknownSicstusImplementationException(sicstusImplementation.toString());
        }
    }

    public static Z3Implementation getImplementation(EZ3Implementations z3Implementation, Workflow workflow, Specification specification, ParametersModel parametersModel) {
        switch (z3Implementation) {
            case DEFAULT:
                return new Z3Implementation(workflow, specification, parametersModel);
            case ONE_TRANSITION_PER_SEGMENT:
                return new OneTransitionPerSegmentZ3Implementation(workflow, specification, parametersModel);
            default:
                throw new UnknownZ3ImplementationException(z3Implementation.toString());
        }
    }

}
