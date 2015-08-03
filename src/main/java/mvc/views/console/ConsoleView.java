package mvc.views.console;

import mvc.controllers.VerificationController;
import mvc.eventsmanagement.VerificationParametersChanged;
import mvc.model.VerificationParameters;
import mvc.views.VerificationView;

public class ConsoleView extends VerificationView {

    public ConsoleView(VerificationController verificationController, VerificationParameters verificationParameters) {
        super(verificationController, verificationParameters);
    }

    @Override
    public void display() {
    }

    @Override
    public void verificationFolderChanged(VerificationParametersChanged event) {
        System.out.println("Verification folder changed : " + ((VerificationParameters) event.getSource()).getVerificationFolder().getAbsolutePath());
    }

}
