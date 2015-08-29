package mvc.controller;

import codegeneration.implementations.sicstus.ESicstusImplementations;
import codegeneration.implementations.z3.EZ3Implementations;
import files.SpecificationFile;
import files.VerificationFolder;
import mvc.events.IVerificationEventListener;
import mvc.events.IParametersEventListener;
import mvc.events.IVerificationFolderChangedListener;
import mvc.models.VerificationModel;
import mvc.models.ParametersModel;
import mvc.views.AbstractVerificationView;
import mvc.views.AbstractFolderSelectionView;
import mvc.views.AbstractParametersView;
import mvc.views.gui.windows.WindowVerificationView;
import mvc.views.gui.windows.WindowFolderSelectionView;
import mvc.views.gui.windows.WindowParametersView;

import java.util.ArrayList;

public class Controller {

    private final VerificationModel verificationModel;
    private final ParametersModel parametersModel;
    private ArrayList<AbstractVerificationView> configurationViews;
    private ArrayList<AbstractFolderSelectionView> folderSelectionViews;
    private ArrayList<AbstractParametersView> parametersViews;

    public Controller(VerificationModel verificationModel, ParametersModel parametersModel) {
        configurationViews = new ArrayList<>();
        folderSelectionViews = new ArrayList<>();
        parametersViews = new ArrayList<>();
        this.verificationModel = verificationModel;
        this.parametersModel = parametersModel;
        configurationViews.add(new WindowVerificationView(this));
        folderSelectionViews.add(new WindowFolderSelectionView(this));
        parametersViews.add(new WindowParametersView(this));
        addListenersToModels();
        parametersModel.update();
    }

    protected void addListenersToModels() {
        for (IVerificationEventListener configurationListener : configurationViews) {
            verificationModel.addConfigurationListener(configurationListener);
        }
        for (IVerificationFolderChangedListener verificationFolderChangedListener : configurationViews) {
            verificationModel.addVerificationFolderListener(verificationFolderChangedListener);
        }
        for(IVerificationFolderChangedListener verificationFolderChangedListener : folderSelectionViews){
            verificationModel.addVerificationFolderListener(verificationFolderChangedListener);
        }
        for (IParametersEventListener parametersListener : parametersViews) {
            parametersModel.addParametersListener(parametersListener);
        }
    }

    public void displayMainViews() {
        for (AbstractVerificationView configurationView : configurationViews) {
            configurationView.display();
        }
    }

    private void displayFolderSelectionViews() {
        for (AbstractFolderSelectionView folderSelectionView : folderSelectionViews) {
            folderSelectionView.display();
        }
    }

    public void displayParametersViews() {
        for (AbstractParametersView parametersView : parametersViews) {
            parametersView.display();
        }
    }

    private void closeParametersViews() {
        for (AbstractParametersView parametersView : parametersViews) {
            parametersView.close();
        }
    }

    /*****************************************************/
    /** NOTIFICATIONS ************************************/
    /*****************************************************/

    public void notifyEditVerificationFolderRequested() {
        displayFolderSelectionViews();
    }

    public void notifyEditParametersRequested() {
        displayParametersViews();
    }

    public void notifyVerificationFolderChanged(VerificationFolder newVerificationFolder) {
        verificationModel.setVerificationFolder(newVerificationFolder);
    }

    public void notifySpecificationFileChanged(SpecificationFile specificationFile) {
        verificationModel.setSpecificationFile(specificationFile);
    }

    public void notifySicstusImplementationChanged(ESicstusImplementations implementation) {
        verificationModel.setSicstusImplementation(implementation);
    }

    public void notifyZ3ImplementationChanged(EZ3Implementations implementation) {
        verificationModel.setZ3Implementation(implementation);
    }

    public void notifyParametersEdited(int maxNodeValuation, int minNumberOfSegments, int maxNumberOfSegments, boolean checkOverApproximation1, boolean checkOverApproximation2, boolean checkOverApproximation3, boolean checkUnderApproximation) {
        parametersModel.setMaxNodeValuation(maxNodeValuation);
        parametersModel.setMinNumberOfSegments(minNumberOfSegments);
        parametersModel.setMaxNumberOfSegments(maxNumberOfSegments);
        parametersModel.setCheckOverApproximation1(checkOverApproximation1);
        parametersModel.setCheckOverApproximation2(checkOverApproximation2);
        parametersModel.setCheckOverApproximation3(checkOverApproximation3);
        parametersModel.setCheckUnderApproximation(checkUnderApproximation);
        closeParametersViews();
    }

    public void notifyParametersEditionCanceled() {
        parametersModel.update();
        closeParametersViews();
    }

    public void notifySicstusVerificationRequired() {
        verificationModel.runSicstusVerification(parametersModel);
    }

    public void notifyZ3VerificationRequired() {
        verificationModel.runZ3Verification(parametersModel);
    }

    public void notifyTimerTicked() {
        verificationModel.fireTimerTicked();
    }

}
