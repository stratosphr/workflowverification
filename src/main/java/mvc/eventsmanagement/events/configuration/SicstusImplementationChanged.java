package mvc.eventsmanagement.events.configuration;

import codegeneration.implementations.sicstus.ESicstusImplementations;
import mvc.model.ConfigurationModel;

public class SicstusImplementationChanged extends AbstractConfigurationEvent {

    private ESicstusImplementations newSicstusImplementation;

    public SicstusImplementationChanged(ConfigurationModel source, ESicstusImplementations newSicstusImplementation) {
        super(source);
        this.newSicstusImplementation = newSicstusImplementation;
    }

    public ESicstusImplementations getNewSicstusImplementation() {
        return newSicstusImplementation;
    }

}
