package mvc.events.events;

import mvc.models.VerificationModel;

public class VerificationStarted extends AbstractEvent {

    public VerificationStarted(VerificationModel source) {
        super(source);
    }

}
