package mvc.model;

import codegeneration.implementations.Implementation;
import codegeneration.implementations.ImplementationFactory;
import codegeneration.implementations.sicstus.ESicstusImplementation;
import codegeneration.implementations.z3.EZ3Implementation;
import files.GeneratedCodeFile.SicstusGeneratedCodeFile;
import files.GeneratedCodeFile.Z3GeneratedCodeFile;
import files.SpecificationFile;
import files.VerificationFolder;
import mvc.eventsmanagement.IConfigurationListener;
import mvc.eventsmanagement.events.configuration.*;
import reports.Report;
import reports.approximations.AbstractApproximation;
import reports.approximations.MultipleSegmentsApproximation;
import reports.approximations.SingleSegmentApproximation;
import verifiers.AbstractVerifier;
import verifiers.IVerificationHandler;
import verifiers.sicstus.SicstusVerifier;
import verifiers.z3.Z3Verifier;

import javax.swing.event.EventListenerList;

public class ConfigurationModel extends AbstractModel implements IVerificationHandler {

    private EventListenerList configurationListeners;
    private VerificationFolder verificationFolder;
    private SpecificationFile specificationFile;
    private ESicstusImplementation sicstusImplementation;
    private EZ3Implementation z3Implementation;
    private Implementation implementation;
    private ParametersModel parametersModel;
    private AbstractVerifier verifier;

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

    private void setImplementation(Implementation implementation) {
        this.implementation = implementation;
    }

    public SicstusGeneratedCodeFile getGeneratedCodeFile(ESicstusImplementation sicstusImplementation) {
        return new SicstusGeneratedCodeFile(getVerificationFolder(), getSpecificationFile(), sicstusImplementation);
    }

    public Z3GeneratedCodeFile getGeneratedCodeFile(EZ3Implementation z3Implementation) {
        return new Z3GeneratedCodeFile(getVerificationFolder(), getSpecificationFile(), z3Implementation);
    }

    public Implementation getImplementation() {
        return implementation;
    }

    public void runSicstusVerification(ParametersModel parametersModel) {
        this.parametersModel = parametersModel;
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
        }
        /*sicstusVerifier.startOverApproximation1Checking(new IVerificationHandler() {
            public void doneChecking(AbstractApproximation result) {
                fireSicstusVerificationDone();
            }
        });
        sicstusVerifier.startOverApproximation2Checking(new IVerificationHandler() {
            public void doneChecking(AbstractApproximation result) {
                fireSicstusVerificationDone();
            }
        });
        sicstusVerifier.startOverApproximation3Checking(new IVerificationHandler() {
            public void doneChecking(AbstractApproximation result) {
                fireSicstusVerificationDone();
            }
        });
        sicstusVerifier.startUnderApproximationChecking(new IVerificationHandler() {
            public void doneChecking(AbstractApproximation result) {
                fireSicstusVerificationDone();
            }
        });*/
    }

    private void fireVerificationStarted() {
        IConfigurationListener[] configurationListeners = this.configurationListeners.getListeners(IConfigurationListener.class);
        for (IConfigurationListener configurationListener : configurationListeners) {
            configurationListener.verificationStarted(new VerificationStarted(this));
        }
    }

    public void runZ3Verification(ParametersModel parametersModel) {
        this.parametersModel = parametersModel;
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
        }
    }

    @Override
    public void doneCheckingOverApproximation1(SingleSegmentApproximation approximation) {
        fireCheckingDone(approximation);
        if (approximation.isSAT()) {
            if (parametersModel.checkOverApproximation2()) {
                verifier.startOverApproximation2Checking(this);
            } else if (parametersModel.checkOverApproximation3()) {
                verifier.startOverApproximation3Checking(this);
            } else if (parametersModel.checkUnderApproximation()) {
                verifier.startUnderApproximationChecking(this);
            }
        }
    }

    @Override
    public void doneCheckingOverApproximation2(SingleSegmentApproximation approximation) {
        fireCheckingDone(approximation);
        if (approximation.isSAT() && !approximation.isValid()) {
            if (parametersModel.checkOverApproximation3()) {
                verifier.startOverApproximation3Checking(this);
            } else if (parametersModel.checkUnderApproximation()) {
                verifier.startUnderApproximationChecking(this);
            }
        }
    }

    @Override
    public void doneCheckingOverApproximation3(MultipleSegmentsApproximation approximation) {
        fireCheckingDone(approximation);
        if (approximation.isSAT()) {
            if (parametersModel.checkUnderApproximation()) {
                verifier.startUnderApproximationChecking(this);
            }
        }
    }

    @Override
    public void doneCheckingUnderApproximation(MultipleSegmentsApproximation approximation) {
        fireCheckingDone(approximation);
    }

    private void fireCheckingDone(AbstractApproximation approximation) {
        IConfigurationListener[] configurationListeners = this.configurationListeners.getListeners(IConfigurationListener.class);
        for (IConfigurationListener configurationListener : configurationListeners) {
            configurationListener.checkingDone(new CheckingDone(this, new Report(this, parametersModel, approximation)));
        }
    }

}
