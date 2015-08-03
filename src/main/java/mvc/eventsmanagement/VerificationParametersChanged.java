package mvc.eventsmanagement;

import mvc.model.VerificationParameters;

import java.util.EventObject;

public class VerificationParametersChanged extends EventObject {

    public VerificationParametersChanged(VerificationParameters verificationParameters) {
        super(verificationParameters);
    }

}
