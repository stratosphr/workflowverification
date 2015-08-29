package mvc.events.events;

import mvc.models.VerificationModel;

public class TimerTicked extends AbstractEvent{

    public TimerTicked(VerificationModel source) {
        super(source);
    }

}
