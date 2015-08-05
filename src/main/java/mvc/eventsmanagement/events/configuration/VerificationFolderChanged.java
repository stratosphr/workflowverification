package mvc.eventsmanagement.events.configuration;

import files.VerificationFolder;
import mvc.model.ConfigurationModel;

public class VerificationFolderChanged extends AbstractVerificationConfigurationEvent {

    private final VerificationFolder newVerificationFolder;

    public VerificationFolderChanged(ConfigurationModel source, VerificationFolder newVerificationFolder) {
        super(source);
        this.newVerificationFolder = newVerificationFolder;
    }

    public VerificationFolder getNewVerificationFolder() {
        return newVerificationFolder;
    }

}
