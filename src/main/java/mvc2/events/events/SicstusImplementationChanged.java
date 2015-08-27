package mvc2.events.events;

import codegeneration.implementations.sicstus.ESicstusImplementations;
import mvc2.models.VerificationModel;

public class SicstusImplementationChanged extends AbstractEvent {

    private final ESicstusImplementations sicstusImplementation;

    public SicstusImplementationChanged(VerificationModel source, ESicstusImplementations sicstusImplementation) {
        super(source);
        this.sicstusImplementation = sicstusImplementation;
    }

    public ESicstusImplementations getSicstusImplementation() {
        return sicstusImplementation;
    }

}
