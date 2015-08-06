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
        representation = "##################\n";
        representation += "### Parameters ###\n";
        representation += "##################\n";
    }

    @Override
    public void display() {
        buildView();
        System.out.println(representation);
    }

    @Override
    public void close() {
    }

    @Override
    public void verificationFolderChanged(VerificationFolderChanged event) {
        System.out.println("VerificationFolderChanged : " + event.getNewVerificationFolder());
    }

    @Override
    public void specificationFileChanged(SpecificationFileChanged event) {
        System.out.println("VerificationFolderChanged : " + event.getNewSpecificationFile());
    }

    @Override
    public void sicstusImplementationChanged(SicstusImplementationChanged event) {
        System.out.println("SicstusImplementationChanged : " + event.getNewSicstusImplementation());
    }

    @Override
    public void z3ImplementationChanged(Z3ImplementationChanged event) {
        System.out.println("Z3ImplementationChanged : " + event.getNewZ3Implementation());
    }

    @Override
    public void sicstusVerificationDone(SicstusVerificationDone event) {
        System.out.println("SicstusVerificationDone");
    }

    @Override
    public void z3VerificationDone(Z3VerificationDone event) {
        System.out.println("Z3VerificationDone");
    }

}
