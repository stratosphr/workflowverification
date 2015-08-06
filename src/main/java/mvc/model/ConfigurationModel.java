package mvc.model;

import codegeneration.implementations.sicstus.ESicstusImplementation;
import codegeneration.implementations.z3.EZ3Implementation;
import codegeneration.writers.SicstusCodeWriter;
import files.SpecificationFile;
import files.VerificationFolder;
import mvc.eventsmanagement.IConfigurationListener;
import mvc.eventsmanagement.events.configuration.*;
import reports.Approximation;
import verifiers.IVerificationHandler;
import verifiers.sicstus.SicstusVerifier;

import javax.swing.event.EventListenerList;

public class ConfigurationModel extends AbstractModel {

    private EventListenerList configurationListeners;
    private VerificationFolder verificationFolder;
    private SpecificationFile specificationFile;
    private ESicstusImplementation sicstusImplementation;
    private EZ3Implementation z3Implementation;

    public ConfigurationModel() {
        this(null, null, ESicstusImplementation.DEFAULT, EZ3Implementation.DEFAULT);
    }

    public ConfigurationModel(VerificationFolder verificationFolder, SpecificationFile specificationFile, ESicstusImplementation sicstusImplementation, EZ3Implementation z3Implementation) {
        configurationListeners = new EventListenerList();
        this.verificationFolder = verificationFolder;
        this.specificationFile = specificationFile;
        this.sicstusImplementation = sicstusImplementation;
        this.z3Implementation = z3Implementation;
    }

    public void addVerificationParametersListener(IConfigurationListener verificationParametersListener) {
        configurationListeners.add(IConfigurationListener.class, verificationParametersListener);
    }

    public VerificationFolder getVerificationFolder() {
        return verificationFolder;
    }

    public void setVerificationFolder(VerificationFolder verificationFolder) {
        this.verificationFolder = verificationFolder;
        fireVerificationFolderChanged();
    }

    public void fireVerificationFolderChanged() {
        IConfigurationListener[] configurationListeners = this.configurationListeners.getListeners(IConfigurationListener.class);
        for (IConfigurationListener configurationListener : configurationListeners) {
            configurationListener.verificationFolderChanged(new VerificationFolderChanged(this, verificationFolder));
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
        IConfigurationListener[] configurationListeners = this.configurationListeners.getListeners(IConfigurationListener.class);
        for (IConfigurationListener configurationListener : configurationListeners) {
            configurationListener.specificationFileChanged(new SpecificationFileChanged(this, specificationFile));
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
        IConfigurationListener[] configurationListeners = this.configurationListeners.getListeners(IConfigurationListener.class);
        for (IConfigurationListener configurationListener : configurationListeners) {
            configurationListener.sicstusImplementationChanged(new SicstusImplementationChanged(this, sicstusImplementation));
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
        IConfigurationListener[] configurationListeners = this.configurationListeners.getListeners(IConfigurationListener.class);
        for (IConfigurationListener configurationListener : configurationListeners) {
            configurationListener.z3ImplementationChanged(new Z3ImplementationChanged(this, z3Implementation));
        }
    }

    public void runSicstusVerification() {
        SicstusCodeWriter sicstusCodeWriter = new SicstusCodeWriter(this);
        sicstusCodeWriter.writeStateEquation();
        SicstusVerifier sicstusVerifier = new SicstusVerifier();
        sicstusVerifier.startOverApproximation1Checking(new IVerificationHandler() {
            public void doneChecking(Approximation result) {
                fireSicstusVerificationDone();
            }
        });
    }

    private void fireSicstusVerificationDone() {
        IConfigurationListener[] configurationListeners = this.configurationListeners.getListeners(IConfigurationListener.class);
        for (IConfigurationListener configurationListener : configurationListeners) {
            configurationListener.sicstusVerificationDone(new SicstusVerificationDone(this));
        }
    }

    public void runZ3Verification() {
        System.out.println("Z3 VERIFICATION...");
        System.out.println("DONE.");
        fireZ3VerificationDone();
    }

    private void fireZ3VerificationDone() {
        IConfigurationListener[] configurationListeners = this.configurationListeners.getListeners(IConfigurationListener.class);
        for (IConfigurationListener configurationListener : configurationListeners) {
            configurationListener.z3VerificationDone(new Z3VerificationDone(this));
        }
    }

}
