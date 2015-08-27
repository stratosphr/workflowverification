package mvc2.events.events;

import mvc2.models.VerificationModel;

public class VerificationStarted extends AbstractEvent {

    public VerificationStarted(VerificationModel source) {
        super(source);
    }

}
