package mvc.model;

import files.SpecificationFile;
import files.VerificationFolder;
import mvc.eventsmanagement.IVerificationParametersListener;
import mvc.eventsmanagement.VerificationParametersChanged;

import javax.swing.event.EventListenerList;

public class VerificationParameters {

    private EventListenerList verificationParametersListeners;
    private VerificationFolder verificationFolder;
    private SpecificationFile specificationFile;

    public VerificationParameters() {
        this(null);
    }

    public VerificationParameters(VerificationFolder verificationFolder) {
        verificationParametersListeners = new EventListenerList();
        this.verificationFolder = verificationFolder;
    }

    public void addVerificationParametersListener(IVerificationParametersListener verificationParametersListener) {
        verificationParametersListeners.add(IVerificationParametersListener.class, verificationParametersListener);
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

    public SpecificationFile getSpecificationFile() {
        return specificationFile;
    }

    public void setSpecificationFile(SpecificationFile specificationFile) {
        this.specificationFile = specificationFile;
        fireSpecificationFileChanged();
    }

    public void fireSpecificationFileChanged() {
        IVerificationParametersListener[] verificationParametersListeners = this.verificationParametersListeners.getListeners(IVerificationParametersListener.class);
        for (IVerificationParametersListener verificationParametersListener : verificationParametersListeners) {
            verificationParametersListener.specificationFileChanged(new VerificationParametersChanged(this));
        }
    }


}
