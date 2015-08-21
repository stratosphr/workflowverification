package mvc2.events.events;

import mvc2.models.ConfigurationModel;

public class VerificationStarted extends AbstractEvent {

    public VerificationStarted(ConfigurationModel source) {
        super(source);
    }

}
