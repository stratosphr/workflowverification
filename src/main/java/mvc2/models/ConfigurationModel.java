package mvc2.models;

import codegeneration.implementations.ImplementationFactory;
import codegeneration.implementations.sicstus.ESicstusImplementations;
import codegeneration.implementations.z3.EZ3Implementations;
import files.GeneratedCodeFile.SicstusGeneratedCodeFile;
import files.GeneratedCodeFile.Z3GeneratedCodeFile;
import files.GeneratedReportFile.SicstusGeneratedReportFile;
import files.GeneratedReportFile.Z3GeneratedReportFile;
import files.SpecificationFile;
import files.VerificationFolder;
import mvc2.events.IConfigurationEventListener;
import mvc2.events.events.*;
import reports.approximations.AbstractApproximation;
import reports.approximations.ApproximationTypes;
import verifiers.IVerificationHandler;
import verifiers.sicstus.SicstusVerifier;
import verifiers.z3.Z3Verifier;

public class ConfigurationModel extends AbstractModel implements IVerificationHandler {

    private VerificationFolder verificationFolder;
    private SpecificationFile specificationFile;
    private ESicstusImplementations sicstusImplementation;
    private EZ3Implementations z3Implementation;
    private SicstusGeneratedCodeFile sicstusGeneratedCodeFile;
    private Z3GeneratedCodeFile z3GeneratedCodeFile;
    private SicstusGeneratedReportFile sicstusGeneratedReportFile;
    private Z3GeneratedReportFile z3GeneratedReportFile;
    private SicstusVerifier sicstusVerifier;
    private Z3Verifier z3Verifier;

    public ConfigurationModel() {
        super();
    }

    public void addConfigurationListener(IConfigurationEventListener configurationListener) {
        eventListeners.add(IConfigurationEventListener.class, configurationListener);
    }

    public void setVerificationFolder(VerificationFolder verificationFolder) {
        if (verificationFolder.isValid()) {
            this.verificationFolder = verificationFolder;
            fireVerificationFolderChanged();
        } else {
            System.err.println("Invalid verification folder !");
        }
    }

    public void setSpecificationFile(SpecificationFile specificationFile) {
        if (specificationFile.isValid()) {
            this.specificationFile = specificationFile;
            fireSpecificationFileChanged();
            updateSicstusGeneratedFiles();
            updateZ3GeneratedFiles();
        } else {
            System.err.println("Invalid specification folder !");
        }
    }

    public void setSicstusImplementation(ESicstusImplementations sicstusImplementation) {
        this.sicstusImplementation = sicstusImplementation;
        updateSicstusGeneratedFiles();
    }

    private void updateSicstusGeneratedFiles() {
        if (verificationFolder != null) {
            this.sicstusGeneratedCodeFile = new SicstusGeneratedCodeFile(verificationFolder, specificationFile, sicstusImplementation);
            this.sicstusGeneratedReportFile = new SicstusGeneratedReportFile(verificationFolder, specificationFile, sicstusImplementation);
            fireSicstusGeneratedFilesChanged();
        }
    }

    public void setZ3Implementation(EZ3Implementations z3Implementation) {
        this.z3Implementation = z3Implementation;
        updateZ3GeneratedFiles();
    }

    private void updateZ3GeneratedFiles() {
        if (verificationFolder != null) {
            this.z3GeneratedCodeFile = new Z3GeneratedCodeFile(verificationFolder, specificationFile, z3Implementation);
            this.z3GeneratedReportFile = new Z3GeneratedReportFile(verificationFolder, specificationFile, z3Implementation);
            fireZ3GeneratedFilesChanged();
        }
    }

    public void runSicstusVerification(ParametersModel parametersModel) {
        this.sicstusVerifier = new SicstusVerifier(sicstusGeneratedCodeFile, ImplementationFactory.getImplementation(sicstusImplementation, verificationFolder.getWorkflowFile().extractWorkflow(), specificationFile.extractSpecification(), parametersModel));
        sicstusVerifier.startChecking(this, parametersModel);
        fireVerificationStarted();
    }

    public void runZ3Verification(ParametersModel parametersModel) {
        this.z3Verifier = new Z3Verifier(z3GeneratedCodeFile, ImplementationFactory.getImplementation(z3Implementation, verificationFolder.getWorkflowFile().extractWorkflow(), specificationFile.extractSpecification(), parametersModel));
        z3Verifier.startChecking(this, parametersModel);
        fireVerificationStarted();
    }

    /*****************************************************/
    /** FIRINGS ******************************************/
    /*****************************************************/

    private void fireVerificationFolderChanged() {
        for (IConfigurationEventListener configurationEventListener : eventListeners.getListeners(IConfigurationEventListener.class)) {
            configurationEventListener.verificationFolderChanged(new VerificationFolderChanged(this, verificationFolder));
        }
    }

    private void fireSpecificationFileChanged() {
        for (IConfigurationEventListener configurationEventListener : eventListeners.getListeners(IConfigurationEventListener.class)) {
            configurationEventListener.specificationFileChanged(new SpecificationFileChanged(this, specificationFile));
        }
    }

    private void fireSicstusGeneratedFilesChanged() {
        for (IConfigurationEventListener configurationEventListener : eventListeners.getListeners(IConfigurationEventListener.class)) {
            configurationEventListener.sicstusGeneratedCodeFilesChanged(new SicstusGeneratedFilesChanged(this, sicstusGeneratedCodeFile, sicstusGeneratedReportFile));
        }
    }

    private void fireZ3GeneratedFilesChanged() {
        for (IConfigurationEventListener configurationEventListener : eventListeners.getListeners(IConfigurationEventListener.class)) {
            configurationEventListener.z3GeneratedCodeFilesChanged(new Z3GeneratedFilesChanged(this, z3GeneratedCodeFile, z3GeneratedReportFile));
        }
    }

    private void fireVerificationStarted() {
        for (IConfigurationEventListener configurationEventListener : eventListeners.getListeners(IConfigurationEventListener.class)) {
            configurationEventListener.verificationStarted(new VerificationStarted(this));
        }
    }

    @Override
    public void fireDoneChecking(ApproximationTypes approximationType, AbstractApproximation approximation) {
        for (IConfigurationEventListener configurationEventListener : eventListeners.getListeners(IConfigurationEventListener.class)) {
            configurationEventListener.doneChecking(new DoneChecking(this, approximationType, approximation));
        }
    }

}
