package mvc.eventsmanagement.events.configuration;

import mvc.model.ConfigurationModel;

import java.util.EventObject;

public abstract class AbstractConfigurationEvent extends EventObject {

    public AbstractConfigurationEvent(ConfigurationModel source) {
        super(source);
    }

}
