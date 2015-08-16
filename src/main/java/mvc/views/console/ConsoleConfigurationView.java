package mvc.views.console;

import mvc.controllers.ConfigurationController;
import mvc.eventsmanagement.events.configuration.*;
import mvc.model.ConfigurationModel;
import mvc.views.AbstractConfigurationView;

public class ConsoleConfigurationView extends AbstractConfigurationView {

    private String representation;

    public ConsoleConfigurationView(ConfigurationController configurationController, ConfigurationModel configurationModel) {
        super(configurationController, configurationModel);
    }

    @Override
    public void buildView() {
    }

    @Override
    public void display() {
        buildView();
    }

    @Override
    public void close() {
    }

    @Override
    public void verificationFolderChanged(VerificationFolderChanged event) {
    }

    @Override
    public void specificationFileChanged(SpecificationFileChanged event) {
    }

    @Override
    public void sicstusImplementationChanged(SicstusImplementationChanged event) {
    }

    @Override
    public void z3ImplementationChanged(Z3ImplementationChanged event) {
    }

    @Override
    public void checkingDone(CheckingDone event) {

    }

    @Override
    public void verificationStarted(VerificationStarted event) {

    }

}
