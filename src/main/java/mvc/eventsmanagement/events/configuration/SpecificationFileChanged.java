package mvc.eventsmanagement.events.configuration;

import files.SpecificationFile;
import mvc.model.ConfigurationModel;

public class SpecificationFileChanged extends AbstractConfigurationEvent {

    private final SpecificationFile newSpecificationFile;

    public SpecificationFileChanged(ConfigurationModel source, SpecificationFile newSpecificationFile) {
        super(source);
        this.newSpecificationFile = newSpecificationFile;
    }

    public SpecificationFile getNewSpecificationFile() {
        return newSpecificationFile;
    }

}
