package mvc.eventsmanagement.events.parameters;

import mvc.model.ParametersModel;

public class MaxNumberOfSegmentsChanged extends VerificationParametersEvent {

    private final int newMaxNumberOfSegments;

    public MaxNumberOfSegmentsChanged(ParametersModel source, int newMaxNumberOfSegments) {
        super(source);
        this.newMaxNumberOfSegments = newMaxNumberOfSegments;
    }

    public int getNewMaxNumberOfSegments() {
        return newMaxNumberOfSegments;
    }

}
