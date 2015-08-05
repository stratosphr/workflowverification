package mvc.views.console;

import mvc.controllers.ConfigurationController;
import mvc.eventsmanagement.events.verificationconfigurationevents.SpecificationFileChanged;
import mvc.eventsmanagement.events.verificationconfigurationevents.VerificationFolderChanged;
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
        System.out.println("CONFIGURATION VIEW CLOSED");
    }

    @Override
    public void verificationFolderChanged(VerificationFolderChanged event) {
        System.out.println("VerificationFolderChanged : " + event.getNewVerificationFolder());
    }

    @Override
    public void specificationFileChanged(SpecificationFileChanged event) {
        System.out.println("VerificationFolderChanged" + event.getNewSpecificationFile());
    }

}
