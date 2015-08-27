package mvc2.events.events;

import mvc2.models.VerificationModel;
import reports.approximations.ApproximationTypes;
import specifications.model.SpecificationType;

public class CheckingStarted extends AbstractEvent {

    private final int segment;
    private final SpecificationType specificationType;
    private final ApproximationTypes approximationType;

    public CheckingStarted(VerificationModel source, SpecificationType specificationType, ApproximationTypes approximationType) {
        this(source, specificationType, approximationType, 0);
    }

    public CheckingStarted(VerificationModel source, SpecificationType specificationType, ApproximationTypes approximationType, int segment) {
        super(source);
        this.specificationType = specificationType;
        this.approximationType = approximationType;
        this.segment = segment;
    }

    public SpecificationType getSpecificationType() {
        return specificationType;
    }

    public ApproximationTypes getApproximationType() {
        return approximationType;
    }

    public int getSegment() {
        return segment;
    }

}
