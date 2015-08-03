package mvc.model;

import files.VerificationFolder;
import mvc.eventsmanagement.IVerificationParametersListener;
import mvc.eventsmanagement.VerificationParametersChanged;

import javax.swing.event.EventListenerList;

public class VerificationParameters {

    private EventListenerList verificationParametersListeners;
    private VerificationFolder verificationFolder;

    public VerificationParameters() {
        this(null);
    }

    public VerificationParameters(VerificationFolder verificationFolder) {
        verificationParametersListeners = new EventListenerList();
        if (verificationFolder == null) {
            System.err.println("Verification folder is null in model VerificationParameters");
        } else {
            this.verificationFolder = verificationFolder;
        }
    }

    public void addVerificationParametersListener(IVerificationParametersListener verificationParametersListener) {
        verificationParametersListeners.add(IVerificationParametersListener.class, verificationParametersListener);
    }

    public void removeVerificationParametersListener(IVerificationParametersListener verificationParametersListener) {
        verificationParametersListeners.remove(IVerificationParametersListener.class, verificationParametersListener);
    }

    public VerificationFolder getVerificationFolder() {
        return verificationFolder;
    }

    public void setVerificationFolder(VerificationFolder verificationFolder) {
        this.verificationFolder = verificationFolder;
        fireVerificationFolderChanged();
    }

    public void fireVerificationFolderChanged() {
        IVerificationParametersListener[] verificationParametersListeners = this.verificationParametersListeners.getListeners(IVerificationParametersListener.class);
        for (IVerificationParametersListener verificationParametersListener : verificationParametersListeners) {
            verificationParametersListener.verificationFolderChanged(new VerificationParametersChanged(this));
        }
    }

}
