package mvc.eventsmanagement.events.verificationparametersevents;

import mvc.model.ParametersModel;

import java.util.EventObject;

public class VerificationParametersEvent extends EventObject {

    public VerificationParametersEvent(ParametersModel source) {
        super(source);
    }

}
