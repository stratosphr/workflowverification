package codegeneration.implementations;

import codegeneration.implementations.sicstus.DefaultSicstusImplementation;
import codegeneration.implementations.sicstus.ESicstusImplementation;
import codegeneration.implementations.sicstus.OneTransitionPerSegmentSicstusImplementation;
import codegeneration.implementations.sicstus.SicstusImplementation;
import codegeneration.implementations.z3.DefaultZ3Implementation;
import codegeneration.implementations.z3.EZ3Implementation;
import codegeneration.implementations.z3.OneTransitionPerSegmentZ3Implementation;
import codegeneration.implementations.z3.Z3Implementation;
import exceptions.UnknownSicstusImplementationException;
import exceptions.UnknownZ3ImplementationException;

public class ImplementationFactory {

    private static Z3Implementation z3Implementation;

    public static SicstusImplementation getSicstusImplementation(ESicstusImplementation sicstusImplementation) {
        switch (sicstusImplementation) {
            case DEFAULT:
                return new DefaultSicstusImplementation();
            case ONE_TRANSITION_PER_SEGMENT:
                return new OneTransitionPerSegmentSicstusImplementation();
            default:
                throw new UnknownSicstusImplementationException(sicstusImplementation.toString());
        }
    }

    public static Z3Implementation getZ3Implementation(EZ3Implementation z3Implementation) {
        switch (z3Implementation) {
            case DEFAULT:
                return new DefaultZ3Implementation();
            case ONE_TRANSITION_PER_SEGMENT:
                return new OneTransitionPerSegmentZ3Implementation();
            default:
                throw new UnknownZ3ImplementationException(z3Implementation.toString());
        }
    }

}
