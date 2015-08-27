package mvc2.events;

import mvc2.events.events.*;

import java.util.EventListener;

public interface IVerificationEventListener extends EventListener {

    void specificationFileChanged(SpecificationFileChanged specificationFileChanged);

    void sicstusGeneratedCodeFilesChanged(SicstusGeneratedFilesChanged sicstusGeneratedFilesChanged);

    void z3GeneratedCodeFilesChanged(Z3GeneratedFilesChanged z3GeneratedFilesChanged);

    void verificationStarted(VerificationStarted verificationStarted);

    void writingStarted(WritingStarted writingStarted);

    void writingDone(WritingDone writingStarted);

    void checkingStarted(CheckingStarted checkingStarted);

    void checkingDone(CheckingDone checkingDone);

}
