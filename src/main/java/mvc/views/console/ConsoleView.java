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
        System.out.println("Verification folder : " + verificationParameters.getVerificationFolder().getAbsolutePath());
    }

    @Override
    public void verificationFolderChanged(VerificationParametersChanged event) {
        verificationParameters = (VerificationParameters) event.getSource();
        display();
    }

    @Override
    public void specificationFileChanged(VerificationParametersChanged event) {
        verificationParameters = (VerificationParameters) event.getSource();
        display();
    }

}
