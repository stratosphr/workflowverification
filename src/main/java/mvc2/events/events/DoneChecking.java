package mvc2.events.events;

import mvc2.models.ConfigurationModel;
import reports.approximations.AbstractApproximation;
import reports.approximations.ApproximationTypes;

public class DoneChecking extends AbstractEvent{

    private final ApproximationTypes approximationType;
    private final AbstractApproximation approximation;

    public DoneChecking(ConfigurationModel source, ApproximationTypes approximationType, AbstractApproximation approximation) {
        super(source);
        this.approximationType = approximationType;
        this.approximation = approximation;
    }

    public ApproximationTypes getApproximationType() {
        return approximationType;
    }

    public AbstractApproximation getApproximation() {
        return approximation;
    }

}
