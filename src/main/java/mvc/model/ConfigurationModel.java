package mvc.model;

import files.SpecificationFile;
import files.VerificationFolder;
import mvc.eventsmanagement.IVerificationConfigurationListener;
import mvc.eventsmanagement.events.verificationconfigurationevents.SpecificationFileChanged;
import mvc.eventsmanagement.events.verificationconfigurationevents.VerificationFolderChanged;

import javax.swing.event.EventListenerList;

public class ConfigurationModel extends AbstractModel {

    private EventListenerList verificationConfigurationListeners;
    private VerificationFolder verificationFolder;
    private SpecificationFile specificationFile;

    public ConfigurationModel() {
        this(null, null);
    }

    public ConfigurationModel(VerificationFolder verificationFolder, SpecificationFile specificationFile) {
        verificationConfigurationListeners = new EventListenerList();
        this.verificationFolder = verificationFolder;
        this.specificationFile = specificationFile;
    }

    public void addVerificationParametersListener(IVerificationConfigurationListener verificationParametersListener) {
        verificationConfigurationListeners.add(IVerificationConfigurationListener.class, verificationParametersListener);
    }

    public VerificationFolder getVerificationFolder() {
        return verificationFolder;
    }

    public void setVerificationFolder(VerificationFolder verificationFolder) {
        this.verificationFolder = verificationFolder;
        fireVerificationFolderChanged();
    }

    public void fireVerificationFolderChanged() {
        IVerificationConfigurationListener[] verificationConfigurationListeners = this.verificationConfigurationListeners.getListeners(IVerificationConfigurationListener.class);
        for (IVerificationConfigurationListener verificationConfigurationListener : verificationConfigurationListeners) {
            verificationConfigurationListener.verificationFolderChanged(new VerificationFolderChanged(this, verificationFolder));
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
        IVerificationConfigurationListener[] verificationConfigurationListeners = this.verificationConfigurationListeners.getListeners(IVerificationConfigurationListener.class);
        for (IVerificationConfigurationListener verificationConfigurationListener : verificationConfigurationListeners) {
            verificationConfigurationListener.specificationFileChanged(new SpecificationFileChanged(this, specificationFile));
        }
    }

}
