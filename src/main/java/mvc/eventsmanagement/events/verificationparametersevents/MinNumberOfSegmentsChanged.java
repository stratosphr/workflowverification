package mvc.eventsmanagement.events.verificationparametersevents;

import mvc.model.ParametersModel;

public class MinNumberOfSegmentsChanged extends VerificationParametersEvent {

    private final int newMinNumberOfSegments;

    public MinNumberOfSegmentsChanged(ParametersModel source, int newMinNumberOfSegments) {
        super(source);
        this.newMinNumberOfSegments = newMinNumberOfSegments;
    }

    public int getNewMinNumberOfSegments() {
        return newMinNumberOfSegments;
    }

}
