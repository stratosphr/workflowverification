package mvc.eventsmanagement.events.verificationconfigurationevents;

import files.SpecificationFile;
import mvc.model.ConfigurationModel;

public class SpecificationFileChanged extends VerificationConfigurationEvent {

    private final SpecificationFile newSpecificationFile;

    public SpecificationFileChanged(ConfigurationModel source, SpecificationFile newSpecificationFile) {
        super(source);
        this.newSpecificationFile = newSpecificationFile;
    }

    public SpecificationFile getNewSpecificationFile() {
        return newSpecificationFile;
    }

}
