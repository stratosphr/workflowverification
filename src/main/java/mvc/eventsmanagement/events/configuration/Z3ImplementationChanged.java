package mvc.eventsmanagement.events.configuration;

import codegeneration.implementations.z3.EZ3Implementation;
import mvc.model.ConfigurationModel;

public class Z3ImplementationChanged extends AbstractVerificationConfigurationEvent {

    private EZ3Implementation newZ3Implementation;

    public Z3ImplementationChanged(ConfigurationModel source, EZ3Implementation newZ3Implementation) {
        super(source);
        this.newZ3Implementation = newZ3Implementation;
    }

    public EZ3Implementation getNewZ3Implementation() {
        return newZ3Implementation;
    }

}
