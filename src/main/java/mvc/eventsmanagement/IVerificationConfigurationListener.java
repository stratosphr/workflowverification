package mvc.eventsmanagement;

import mvc.eventsmanagement.events.configuration.SicstusImplementationChanged;
import mvc.eventsmanagement.events.configuration.SpecificationFileChanged;
import mvc.eventsmanagement.events.configuration.VerificationFolderChanged;
import mvc.eventsmanagement.events.configuration.Z3ImplementationChanged;

import java.util.EventListener;

public interface IVerificationConfigurationListener extends EventListener {

    void verificationFolderChanged(VerificationFolderChanged event);

    void specificationFileChanged(SpecificationFileChanged event);

    void sicstusImplementationChanged(SicstusImplementationChanged event);

    void z3ImplementationChanged(Z3ImplementationChanged event);

}
