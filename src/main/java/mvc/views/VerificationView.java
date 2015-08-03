package mvc.views;

import mvc.controllers.VerificationController;
import mvc.eventsmanagement.IVerificationParametersListener;
import mvc.model.VerificationParameters;

public abstract class VerificationView implements IVerificationParametersListener {

    private VerificationController verificationController;
    public VerificationParameters verificationParameters;

    public VerificationView(VerificationController verificationController, VerificationParameters verificationParameters) {
        this.verificationController = verificationController;
        this.verificationParameters = verificationParameters;
    }

    public VerificationController getVerificationController() {
        return verificationController;
    }

    public abstract void display();

}
