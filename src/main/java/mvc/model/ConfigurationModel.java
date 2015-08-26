package mvc.model;

import codegeneration.implementations.AbstractImplementation;
import codegeneration.implementations.sicstus.ESicstusImplementations;
import codegeneration.implementations.z3.EZ3Implementations;
import files.GeneratedCodeFile.SicstusGeneratedCodeFile;
import files.GeneratedCodeFile.Z3GeneratedCodeFile;
import files.SpecificationFile;
import files.VerificationFolder;
import mvc.eventsmanagement.IConfigurationListener;
import mvc.eventsmanagement.events.configuration.*;
import verifiers.AbstractVerifier;

import javax.swing.event.EventListenerList;

public class ConfigurationModel extends AbstractModel {

    private EventListenerList configurationListeners;
    private VerificationFolder verificationFolder;
    private SpecificationFile specificationFile;
    private ESicstusImplementations sicstusImplementation;
    private EZ3Implementations z3Implementation;
    private AbstractImplementation implementation;
    private ParametersModel parametersModel;
    private AbstractVerifier verifier;

    public ConfigurationModel() {
        this(null, null, ESicstusImplementations.DEFAULT, EZ3Implementations.DEFAULT);
    }

    public ConfigurationModel(VerificationFolder verificationFolder, SpecificationFile specificationFile, ESicstusImplementations sicstusImplementation, EZ3Implementations z3Implementation) {
        configurationListeners = new EventListenerList();
        this.verificationFolder = verificationFolder;
        this.specificationFile = specificationFile;
        this.sicstusImplementation = sicstusImplementation;
        this.z3Implementation = z3Implementation;
    }

    public void addConfigurationListener(IConfigurationListener configurationListener) {
        configurationListeners.add(IConfigurationListener.class, configurationListener);
    }

    public VerificationFolder getVerificationFolder() {
        return verificationFolder;
    }

    public void setVerificationFolder(VerificationFolder verificationFolder) {
        if (verificationFolder.isValid()) {
            this.verificationFolder = verificationFolder;
            fireVerificationFolderChanged();
        }
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

    public ESicstusImplementations getSicstusImplementation() {
        return sicstusImplementation;
    }

    public void setSicstusImplementation(ESicstusImplementations sicstusImplementation) {
        this.sicstusImplementation = sicstusImplementation;
        fireSicstusImplementationChanged();
    }

    public void fireSicstusImplementationChanged() {
        IConfigurationListener[] configurationListeners = this.configurationListeners.getListeners(IConfigurationListener.class);
        for (IConfigurationListener configurationListener : configurationListeners) {
            configurationListener.sicstusImplementationChanged(new SicstusImplementationChanged(this, sicstusImplementation));
        }
    }

    public EZ3Implementations getZ3Implementation() {
        return z3Implementation;
    }

    public void setZ3Implementation(EZ3Implementations z3Implementation) {
        this.z3Implementation = z3Implementation;
        fireZ3ImplementationChanged();
    }

    public void fireZ3ImplementationChanged() {
        IConfigurationListener[] configurationListeners = this.configurationListeners.getListeners(IConfigurationListener.class);
        for (IConfigurationListener configurationListener : configurationListeners) {
            configurationListener.z3ImplementationChanged(new Z3ImplementationChanged(this, z3Implementation));
        }
    }

    private void setImplementation(AbstractImplementation implementation) {
        this.implementation = implementation;
    }

    public SicstusGeneratedCodeFile getGeneratedCodeFile(ESicstusImplementations sicstusImplementation) {
        return new SicstusGeneratedCodeFile(getVerificationFolder(), getSpecificationFile(), sicstusImplementation);
    }

    public Z3GeneratedCodeFile getGeneratedCodeFile(EZ3Implementations z3Implementation) {
        return new Z3GeneratedCodeFile(getVerificationFolder(), getSpecificationFile(), z3Implementation);
    }

    public AbstractImplementation getImplementation() {
        return implementation;
    }

    public void runSicstusVerification(ParametersModel parametersModel) {
        /*this.parametersModel = parametersModel;
        setImplementation(ImplementationFactory.getImplementation(getSicstusImplementation(), getVerificationFolder().getWorkflowFile().extractWorkflow(), getSpecificationFile().extractSpecification(), parametersModel));
        this.verifier = new SicstusVerifier(getGeneratedCodeFile(getSicstusImplementation()), getImplementation());
        fireVerificationStarted();
        if (parametersModel.checkOverApproximation1()) {
            verifier.startOverApproximation1Checking(this);
        } else if (parametersModel.checkOverApproximation2()) {
            verifier.startOverApproximation2Checking(this);
        } else if (parametersModel.checkOverApproximation3()) {
            verifier.startOverApproximation3Checking(this);
        } else if (parametersModel.checkUnderApproximation()) {
            verifier.startUnderApproximationChecking(this);
        }*/
    }

    private void fireVerificationStarted() {
        IConfigurationListener[] configurationListeners = this.configurationListeners.getListeners(IConfigurationListener.class);
        for (IConfigurationListener configurationListener : configurationListeners) {
            configurationListener.verificationStarted(new VerificationStarted(this));
        }
    }

    public void runZ3Verification(ParametersModel parametersModel) {
        /*this.parametersModel = parametersModel;
        setImplementation(ImplementationFactory.getImplementation(getZ3Implementation(), getVerificationFolder().getWorkflowFile().extractWorkflow(), getSpecificationFile().extractSpecification(), parametersModel));
        this.verifier = new Z3Verifier(getGeneratedCodeFile(getZ3Implementation()), getImplementation());
        fireVerificationStarted();
        if (parametersModel.checkOverApproximation1()) {
            verifier.startOverApproximation1Checking(this);
        } else if (parametersModel.checkOverApproximation2()) {
            verifier.startOverApproximation2Checking(this);
        } else if (parametersModel.checkOverApproximation3()) {
            verifier.startOverApproximation3Checking(this);
        } else if (parametersModel.checkUnderApproximation()) {
            verifier.startUnderApproximationChecking(this);
        }*/
    }

}
