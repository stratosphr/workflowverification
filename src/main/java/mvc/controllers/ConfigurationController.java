package mvc.controllers;

import codegeneration.implementations.sicstus.ESicstusImplementation;
import codegeneration.implementations.z3.EZ3Implementation;
import files.SpecificationFile;
import files.VerificationFolder;
import mvc.model.ConfigurationModel;
import mvc.model.ParametersModel;
import mvc.views.console.ConsoleConfigurationView;
import mvc.views.console.ConsoleParametersView;
import mvc.views.gui.WindowConfigurationView;
import mvc.views.gui.WindowParametersView;

public class ConfigurationController extends AbstractController {

    private ConfigurationModel configurationModel;
    private ParametersModel parametersModel;
    private WindowConfigurationView windowMainView;
    private ConsoleParametersView consoleParametersView;
    private WindowParametersView windowParametersView;
    private ConsoleConfigurationView consoleMainView;

    public ConfigurationController(ConfigurationModel configurationModel, ParametersModel parametersModel) {
        this.configurationModel = configurationModel;
        this.parametersModel = parametersModel;
        consoleMainView = new ConsoleConfigurationView(this, configurationModel);
        windowMainView = new WindowConfigurationView(this, configurationModel);
        consoleParametersView = new ConsoleParametersView(this, parametersModel);
        windowParametersView = new WindowParametersView(this, parametersModel);
        addListenersToModel();
    }

    private void addListenersToModel() {
        configurationModel.addConfigurationListener(consoleMainView);
        configurationModel.addConfigurationListener(windowMainView);
        parametersModel.addParametersListener(consoleParametersView);
        parametersModel.addParametersListener(windowParametersView);
    }

    @Override
    public void displayMainViews() {
        consoleMainView.display();
        windowMainView.display();
    }

    @Override
    public void displayParametersViews() {
        consoleParametersView.display();
        windowParametersView.display();
    }

    @Override
    public void closeMainViews() {
        consoleMainView.close();
        windowMainView.close();
    }

    @Override
    public void closeParametersViews() {
        consoleParametersView.close();
        windowParametersView.close();
    }

    @Override
    public ConfigurationModel getModel() {
        return configurationModel;
    }

    public void notifyVerificationFolderChanged(VerificationFolder newVerificationFolder) {
        configurationModel.setVerificationFolder(newVerificationFolder);
    }

    public void notifySpecificationFileChanged(SpecificationFile newSpecificationFile) {
        configurationModel.setSpecificationFile(newSpecificationFile);
    }

    public void notifyParametersEditionRequired() {
        displayParametersViews();
    }

    public void notifySicstusImplementationChanged(ESicstusImplementation newSicstusImplementation) {
        configurationModel.setSicstusImplementation(newSicstusImplementation);
    }

    public void notifyZ3ImplementationChanged(EZ3Implementation newZ3Implementation) {
        configurationModel.setZ3Implementation(newZ3Implementation);
    }

    public void notifySicstusVerificationRequired() {
        configurationModel.runSicstusVerification(parametersModel);
    }

    public void notifyZ3VerificationRequired() {
        configurationModel.runZ3Verification(parametersModel);
    }

    public void notifyMaxNodeValuationChanged(int newMaxNodeValuation) {
        parametersModel.setMaxNodeValuation(newMaxNodeValuation);
    }

    public void notifyMinNumberOfSegmentsChanged(int minNumberOfSegments) {
        parametersModel.setMinNumberOfSegments(minNumberOfSegments);
    }

    public void notifyMaxNumberOfSegmentsChanged(int maxNumberOfSegments) {
        parametersModel.setMaxNumberOfSegments(maxNumberOfSegments);
    }

    public void notifyCheckOverApproximation1Changed(boolean isSelected) {
        parametersModel.setCheckOverApproximation1(isSelected);
    }

    public void notifyCheckOverApproximation2Changed(boolean isSelected) {
        parametersModel.setCheckOverApproximation2(isSelected);
    }

    public void notifyCheckOverApproximation3Changed(boolean isSelected) {
        parametersModel.setCheckOverApproximation3(isSelected);
    }

    public void notifyCheckUnderApproximationChanged(boolean isSelected) {
        parametersModel.setCheckUnderApproximation(isSelected);
    }

    public void notifyParametersEditionValidated() {
        closeParametersViews();
    }

    public void notifyParametersEditionAborted() {
        notifyMaxNodeValuationChanged(parametersModel.getMaxNodeValuation());
        notifyMinNumberOfSegmentsChanged(parametersModel.getMinNumberOfSegments());
        notifyMaxNumberOfSegmentsChanged(parametersModel.getMaxNumberOfSegments());
        notifyCheckOverApproximation1Changed(parametersModel.checkOverApproximation1());
        notifyCheckOverApproximation2Changed(parametersModel.checkOverApproximation2());
        notifyCheckOverApproximation3Changed(parametersModel.checkOverApproximation3());
        notifyCheckUnderApproximationChanged(parametersModel.checkUnderApproximation());
        closeParametersViews();
    }

}
