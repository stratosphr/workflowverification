package mvc.events.events;

import mvc.models.VerificationModel;
import reports.approximations.ApproximationTypes;
import specifications.model.SpecificationType;

public class WritingStarted extends AbstractEvent {

    private final SpecificationType specificationType;
    private final ApproximationTypes approximationType;
    private final int segment;

    public WritingStarted(VerificationModel source, SpecificationType specificationType, ApproximationTypes approximationType) {
        this(source, specificationType, approximationType, 0);
    }

    public WritingStarted(VerificationModel source, SpecificationType specificationType, ApproximationTypes approximationType, int segment) {
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
