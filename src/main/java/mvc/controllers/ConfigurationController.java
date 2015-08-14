package mvc.controllers;

import codegeneration.implementations.sicstus.ESicstusImplementation;
import codegeneration.implementations.z3.EZ3Implementation;
import files.SpecificationFile;
import files.VerificationFolder;
import mvc.model.ConfigurationModel;
import mvc.model.ParametersModel;
import mvc.views.AbstractConfigurationView;
import mvc.views.console.ConsoleConfigurationView;
import mvc.views.gui.WindowConfigurationView;

public class ConfigurationController extends AbstractController {

    private ParametersController parametersController;
    private ConfigurationModel configurationModel;
    private WindowConfigurationView windowView;
    private AbstractConfigurationView consoleView;

    public ConfigurationController(ConfigurationModel configurationModel, ParametersModel parametersModel) {
        this.configurationModel = configurationModel;
        consoleView = new ConsoleConfigurationView(this, configurationModel);
        windowView = new WindowConfigurationView(this, configurationModel);
        parametersController = new ParametersController(parametersModel);
        addListenersToModel();
    }

    private void addListenersToModel() {
        configurationModel.addVerificationParametersListener(consoleView);
        configurationModel.addVerificationParametersListener(windowView);
    }

    @Override
    public void displayViews() {
        consoleView.display();
        windowView.display();
    }

    @Override
    public void closeViews() {
        consoleView.close();
        windowView.close();
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
        parametersController.displayViews();
    }

    public void notifySicstusImplementationChanged(ESicstusImplementation newSicstusImplementation) {
        configurationModel.setSicstusImplementation(newSicstusImplementation);
    }

    public void notifyZ3ImplementationChanged(EZ3Implementation newZ3Implementation) {
        configurationModel.setZ3Implementation(newZ3Implementation);
    }

    public void notifySicstusVerificationRequired() {
        configurationModel.runSicstusVerification();
    }

    public void notifyZ3VerificationRequired() {
        configurationModel.runZ3Verification();
    }

}
