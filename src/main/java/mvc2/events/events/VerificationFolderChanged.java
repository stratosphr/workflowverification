package mvc2.events.events;

import files.VerificationFolder;
import mvc2.models.ConfigurationModel;

public class VerificationFolderChanged extends AbstractEvent {

    private final VerificationFolder verificationFolder;

    public VerificationFolderChanged(ConfigurationModel source, VerificationFolder verificationFolder) {
        super(source);
        this.verificationFolder = verificationFolder;
    }

    public VerificationFolder getVerificationFolder() {
        return verificationFolder;
    }

}
