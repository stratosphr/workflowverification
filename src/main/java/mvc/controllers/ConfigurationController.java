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
        configurationModel.addParametersListener(consoleParametersView);
        configurationModel.addParametersListener(windowParametersView);
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
    public void closeParametersView() {
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

    public void notifyParametersEditionValidated() {
        closeParametersView();
    }

    public void notifyParametersEditionAborted() {
        closeParametersView();
    }

}
