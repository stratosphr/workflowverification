package mvc.eventsmanagement;

import java.util.EventListener;

public interface IVerificationParametersListener extends EventListener {

    void verificationFolderChanged(VerificationParametersChanged event);

    void specificationFileChanged(VerificationParametersChanged event);

}
