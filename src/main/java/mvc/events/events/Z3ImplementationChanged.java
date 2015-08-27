package mvc.events.events;

import codegeneration.implementations.z3.EZ3Implementations;
import mvc.models.VerificationModel;

public class Z3ImplementationChanged extends AbstractEvent {

    private final EZ3Implementations z3Implementation;

    public Z3ImplementationChanged(VerificationModel source, EZ3Implementations z3Implementation) {
        super(source);
        this.z3Implementation = z3Implementation;
    }

    public EZ3Implementations getZ3Implementation() {
        return z3Implementation;
    }

}
