package mvc2.events;

import mvc2.events.events.VerificationFolderChanged;

import java.util.EventListener;

public interface IVerificationFolderChangedListener extends EventListener {

    void verificationFolderChanged(VerificationFolderChanged verificationFolderChanged);

}
