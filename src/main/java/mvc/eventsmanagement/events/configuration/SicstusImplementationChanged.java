package mvc.eventsmanagement.events.configuration;

import codegeneration.implementations.sicstus.ESicstusImplementation;
import mvc.model.ConfigurationModel;

public class SicstusImplementationChanged extends AbstractConfigurationEvent {

    private ESicstusImplementation newSicstusImplementation;

    public SicstusImplementationChanged(ConfigurationModel source, ESicstusImplementation newSicstusImplementation) {
        super(source);
        this.newSicstusImplementation = newSicstusImplementation;
    }

    public ESicstusImplementation getNewSicstusImplementation() {
        return newSicstusImplementation;
    }

}
