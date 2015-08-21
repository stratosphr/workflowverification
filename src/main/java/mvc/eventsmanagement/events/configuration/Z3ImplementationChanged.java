package mvc.eventsmanagement.events.configuration;

import codegeneration.implementations.z3.EZ3Implementations;
import mvc.model.ConfigurationModel;

public class Z3ImplementationChanged extends AbstractConfigurationEvent {

    private EZ3Implementations newZ3Implementation;

    public Z3ImplementationChanged(ConfigurationModel source, EZ3Implementations newZ3Implementation) {
        super(source);
        this.newZ3Implementation = newZ3Implementation;
    }

    public EZ3Implementations getNewZ3Implementation() {
        return newZ3Implementation;
    }

}
