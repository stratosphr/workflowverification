package mvc.model;

import codegeneration.implementations.sicstus.ESicstusImplementation;
import codegeneration.implementations.z3.EZ3Implementation;
import files.SpecificationFile;
import files.VerificationFolder;
import mvc.eventsmanagement.IVerificationConfigurationListener;
import mvc.eventsmanagement.events.configuration.SicstusImplementationChanged;
import mvc.eventsmanagement.events.configuration.SpecificationFileChanged;
import mvc.eventsmanagement.events.configuration.VerificationFolderChanged;
import mvc.eventsmanagement.events.configuration.Z3ImplementationChanged;

import javax.swing.event.EventListenerList;

public class ConfigurationModel extends AbstractModel {

    private EventListenerList verificationConfigurationListeners;
    private VerificationFolder verificationFolder;
    private SpecificationFile specificationFile;
    private ESicstusImplementation sicstusImplementation;
    private EZ3Implementation z3Implementation;

    public ConfigurationModel() {
        this(null, null, ESicstusImplementation.DEFAULT, EZ3Implementation.DEFAULT);
    }

    public ConfigurationModel(VerificationFolder verificationFolder, SpecificationFile specificationFile, ESicstusImplementation sicstusImplementation, EZ3Implementation z3Implementation) {
        verificationConfigurationListeners = new EventListenerList();
        this.verificationFolder = verificationFolder;
        this.specificationFile = specificationFile;
        this.sicstusImplementation = sicstusImplementation;
        this.z3Implementation = z3Implementation;
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

    public ESicstusImplementation getSicstusImplementation() {
        return sicstusImplementation;
    }

    public void setSicstusImplementation(ESicstusImplementation sicstusImplementation) {
        this.sicstusImplementation = sicstusImplementation;
        fireSicstusImplementationChanged();
    }

    public void fireSicstusImplementationChanged() {
        IVerificationConfigurationListener[] verificationConfigurationListeners = this.verificationConfigurationListeners.getListeners(IVerificationConfigurationListener.class);
        for (IVerificationConfigurationListener verificationConfigurationListener : verificationConfigurationListeners) {
            verificationConfigurationListener.sicstusImplementationChanged(new SicstusImplementationChanged(this, sicstusImplementation));
        }
    }

    public EZ3Implementation getZ3Implementation() {
        return z3Implementation;
    }

    public void setZ3Implementation(EZ3Implementation z3Implementation) {
        this.z3Implementation = z3Implementation;
        fireZ3ImplementationChanged();
    }

    public void fireZ3ImplementationChanged() {
        IVerificationConfigurationListener[] verificationConfigurationListeners = this.verificationConfigurationListeners.getListeners(IVerificationConfigurationListener.class);
        for (IVerificationConfigurationListener verificationConfigurationListener : verificationConfigurationListeners) {
            verificationConfigurationListener.z3ImplementationChanged(new Z3ImplementationChanged(this, z3Implementation));
        }
    }

}
