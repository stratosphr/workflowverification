package mvc.eventsmanagement.events.verificationconfigurationevents;

import mvc.model.ConfigurationModel;

import java.util.EventObject;

public class VerificationConfigurationEvent extends EventObject {

    public VerificationConfigurationEvent(ConfigurationModel source) {
        super(source);
    }

}
