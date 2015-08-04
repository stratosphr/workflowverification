package codegeneration.implementations;

import codegeneration.implementations.sicstus.DefaultSicstusImplementation;
import codegeneration.implementations.sicstus.ESicstusImplementation;
import codegeneration.implementations.sicstus.OneTransitionPerSegmentSicstusImplementation;
import exceptions.UnknownSicstusImplementationException;

public class ImplementationFactory {

    public static Implementation getSicstusImplementation(ESicstusImplementation sicstusImplementation) {
        switch (sicstusImplementation) {
            case DEFAULT:
                return new DefaultSicstusImplementation();
            case ONE_TRANSITION_PER_SEGMENT:
                return new OneTransitionPerSegmentSicstusImplementation();
            default:
                throw new UnknownSicstusImplementationException(sicstusImplementation.toString());
        }
    }

}
