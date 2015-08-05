package mvc.eventsmanagement.events.configuration;

import mvc.model.ConfigurationModel;

import java.util.EventObject;

public abstract class AbstractVerificationConfigurationEvent extends EventObject {

    public AbstractVerificationConfigurationEvent(ConfigurationModel source) {
        super(source);
    }

}
