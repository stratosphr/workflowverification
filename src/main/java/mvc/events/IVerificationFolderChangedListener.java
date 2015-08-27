package mvc.events;

import mvc.events.events.VerificationFolderChanged;

import java.util.EventListener;

public interface IVerificationFolderChangedListener extends EventListener {

    void verificationFolderChanged(VerificationFolderChanged verificationFolderChanged);

}
