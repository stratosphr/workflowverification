package mvc2.controller;

import codegeneration.implementations.sicstus.ESicstusImplementations;
import codegeneration.implementations.z3.EZ3Implementations;
import files.SpecificationFile;
import files.VerificationFolder;
import mvc2.events.IConfigurationEventListener;
import mvc2.events.IParametersEventListener;
import mvc2.models.ConfigurationModel;
import mvc2.models.ParametersModel;
import mvc2.views.AbstractConfigurationView;
import mvc2.views.AbstractFolderSelectionView;
import mvc2.views.AbstractParametersView;
import mvc2.views.gui.windows.WindowConfigurationView;
import mvc2.views.gui.windows.WindowFolderSelectionView;
import mvc2.views.gui.windows.WindowParametersView;

import java.util.ArrayList;

public class Controller {

    private final ConfigurationModel configurationModel;
    private final ParametersModel parametersModel;
    private ArrayList<AbstractConfigurationView> configurationViews;
    private ArrayList<AbstractFolderSelectionView> folderSelectionViews;
    private ArrayList<AbstractParametersView> parametersViews;

    public Controller(ConfigurationModel configurationModel, ParametersModel parametersModel) {
        configurationViews = new ArrayList<>();
        folderSelectionViews = new ArrayList<>();
        parametersViews = new ArrayList<>();
        this.configurationModel = configurationModel;
        this.parametersModel = parametersModel;
        configurationViews.add(new WindowConfigurationView(this));
        folderSelectionViews.add(new WindowFolderSelectionView(this));
        parametersViews.add(new WindowParametersView(this));
        addListenersToModels();
        parametersModel.update();
    }

    protected void addListenersToModels() {
        for (IConfigurationEventListener configurationListener : configurationViews) {
            configurationModel.addConfigurationListener(configurationListener);
        }
        for (IParametersEventListener parametersListener : parametersViews) {
            parametersModel.addParametersListener(parametersListener);
        }
    }

    public void displayMainViews() {
        for (AbstractConfigurationView configurationView : configurationViews) {
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
        for(AbstractParametersView parametersView : parametersViews){
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
        configurationModel.setVerificationFolder(newVerificationFolder);
    }

    public void notifySpecificationFileChanged(SpecificationFile specificationFile) {
        configurationModel.setSpecificationFile(specificationFile);
    }

    public void notifySicstusImplementationChanged(ESicstusImplementations implementation) {
        configurationModel.setSicstusImplementation(implementation);
    }

    public void notifyZ3ImplementationChanged(EZ3Implementations implementation) {
        configurationModel.setZ3Implementation(implementation);
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
        configurationModel.runSicstusVerification(parametersModel);
    }

    public void notifyZ3VerificationRequired() {
        configurationModel.runZ3Verification(parametersModel);
    }

}
