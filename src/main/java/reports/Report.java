package reports;

import codegeneration.implementations.AbstractImplementation;
import reports.approximations.AbstractApproximation;
import reports.approximations.ApproximationTypes;
import reports.approximations.MultipleSegmentsApproximation;

public class Report {

    private final AbstractImplementation implementation;
    private final ApproximationTypes approximationType;
    private final AbstractApproximation approximation;
    private int nbSegments;
    private long time;

    public Report(AbstractImplementation implementation, ApproximationTypes approximationType, AbstractApproximation approximation, long time) {
        this.implementation = implementation;
        this.approximationType = approximationType;
        this.approximation = approximation;
        this.time = time;
    }

    public ApproximationTypes getApproximationType() {
        return approximationType;
    }

    public AbstractImplementation getImplementation() {
        return implementation;
    }

    public int getNbSegments() {
        if (approximationType == ApproximationTypes.OVER_APPROXIMATION_3 || approximationType == ApproximationTypes.UNDER_APPROXIMATION) {
            return ((MultipleSegmentsApproximation) approximation).getNbSegments();
        } else {
            return 0;
        }
    }

    public AbstractApproximation getApproximation() {
        return approximation;
    }

    @Override
    public String toString() {
        return "SAT : " + approximation.isSAT() + "\n" + approximation.toString();
    }

    public long getTime() {
        return time;
    }

}
