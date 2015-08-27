package mvc.events.events;

import files.VerificationFolder;
import mvc.models.VerificationModel;

public class VerificationFolderChanged extends AbstractEvent {

    private final VerificationFolder verificationFolder;

    public VerificationFolderChanged(VerificationModel source, VerificationFolder verificationFolder) {
        super(source);
        this.verificationFolder = verificationFolder;
    }

    public VerificationFolder getVerificationFolder() {
        return verificationFolder;
    }

}
