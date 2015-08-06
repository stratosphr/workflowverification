package mvc.eventsmanagement;

import mvc.eventsmanagement.events.configuration.*;

import java.util.EventListener;

public interface IConfigurationListener extends EventListener {

    void verificationFolderChanged(VerificationFolderChanged event);

    void specificationFileChanged(SpecificationFileChanged event);

    void sicstusImplementationChanged(SicstusImplementationChanged event);

    void z3ImplementationChanged(Z3ImplementationChanged event);

    void sicstusVerificationDone(SicstusVerificationDone event);

    void z3VerificationDone(Z3VerificationDone event);

}
