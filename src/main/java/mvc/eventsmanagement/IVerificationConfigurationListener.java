package mvc.eventsmanagement;

import mvc.eventsmanagement.events.verificationconfigurationevents.SpecificationFileChanged;
import mvc.eventsmanagement.events.verificationconfigurationevents.VerificationFolderChanged;

import java.util.EventListener;

public interface IVerificationConfigurationListener extends EventListener {

    void verificationFolderChanged(VerificationFolderChanged event);

    void specificationFileChanged(SpecificationFileChanged event);

}
