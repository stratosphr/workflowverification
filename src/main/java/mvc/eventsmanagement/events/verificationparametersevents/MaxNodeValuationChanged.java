package mvc.eventsmanagement.events.verificationparametersevents;

import mvc.model.ParametersModel;

public class MaxNodeValuationChanged extends VerificationParametersEvent {

    private final int newMaxNodeValuation;

    public MaxNodeValuationChanged(ParametersModel source, int newMaxNodeValuation) {
        super(source);
        this.newMaxNodeValuation = newMaxNodeValuation;
    }

    public int getNewMaxNodeValuation() {
        return newMaxNodeValuation;
    }

}
