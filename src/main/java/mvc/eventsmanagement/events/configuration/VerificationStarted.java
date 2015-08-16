package mvc.eventsmanagement.events.configuration;

import mvc.model.ConfigurationModel;

public class VerificationStarted extends AbstractConfigurationEvent {

    public VerificationStarted(ConfigurationModel configurationModel) {
        super(configurationModel);
    }

}
