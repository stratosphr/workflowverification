package reports;

import codegeneration.implementations.AbstractImplementation;
import reports.approximations.AbstractApproximation;
import reports.approximations.ApproximationTypes;

public class Report {

    private final AbstractImplementation implementation;
    private final ApproximationTypes approximationType;
    private final AbstractApproximation approximation;

    public Report(AbstractImplementation implementation, ApproximationTypes approximationType, AbstractApproximation approximation) {
        this.implementation = implementation;
        this.approximationType = approximationType;
        this.approximation = approximation;
    }

    public ApproximationTypes getApproximationType() {
        return approximationType;
    }

    public AbstractImplementation getImplementation() {
        return implementation;
    }

    @Override
    public String toString() {
        return approximation.toString();
    }

}
