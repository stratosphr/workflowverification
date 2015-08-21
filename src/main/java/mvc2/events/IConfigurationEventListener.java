package mvc2.events;

import mvc2.events.events.*;

import java.util.EventListener;

public interface IConfigurationEventListener extends EventListener {

    void verificationFolderChanged(VerificationFolderChanged verificationFolderChanged);

    void specificationFileChanged(SpecificationFileChanged specificationFileChanged);

    void sicstusGeneratedCodeFilesChanged(SicstusGeneratedFilesChanged sicstusGeneratedFilesChanged);

    void z3GeneratedCodeFilesChanged(Z3GeneratedFilesChanged z3GeneratedFilesChanged);

    void verificationStarted(VerificationStarted verificationStarted);

    void doneChecking(DoneChecking doneChecking);

}
