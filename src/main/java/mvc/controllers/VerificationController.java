package mvc.controllers;

import files.VerificationFolder;
import mvc.model.VerificationParameters;
import mvc.views.VerificationView;
import mvc.views.console.ConsoleView;
import mvc.views.gui.MainWindow;

public class VerificationController {

    private VerificationParameters verificationParameters;
    private final MainWindow windowView;
    private VerificationView consoleView;

    public VerificationController(VerificationParameters verificationParameters) {
        this.verificationParameters = verificationParameters;
        consoleView = new ConsoleView(this, verificationParameters);
        windowView = new MainWindow(this, verificationParameters);
        addListenersToModel();
    }

    private void addListenersToModel() {
        verificationParameters.addVerificationParametersListener(consoleView);
        verificationParameters.addVerificationParametersListener(windowView);
    }

    public void displayViews() {
        consoleView.display();
        windowView.display();
    }

    public void notifyVerificationFolderChanged(VerificationFolder verificationFolder){
        verificationParameters.setVerificationFolder(verificationFolder);
    }

}
