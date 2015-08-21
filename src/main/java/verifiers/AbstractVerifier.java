package verifiers;

import codegeneration.CodeWriter;
import codegeneration.implementations.AbstractImplementation;
import files.GeneratedCodeFile.GeneratedCodeFile;
import mvc2.models.ConfigurationModel;
import mvc2.models.ParametersModel;
import reports.approximations.ApproximationTypes;
import reports.approximations.MultipleSegmentsApproximation;
import reports.approximations.SingleSegmentApproximation;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public abstract class AbstractVerifier {

    protected final GeneratedCodeFile generatedCodeFile;
    protected final AbstractImplementation implementation;
    private ParametersModel parametersModel;
    private int minNumberOfSegments;

    public AbstractVerifier(GeneratedCodeFile generatedCodeFile, AbstractImplementation implementation) {
        this.generatedCodeFile = generatedCodeFile;
        this.implementation = implementation;
        CodeWriter codeWriter = new CodeWriter(generatedCodeFile, implementation);
        codeWriter.writeHeader();
        codeWriter.writeInitialMarking();
        codeWriter.writeFinalMarking();
        codeWriter.writeStateEquation();
        codeWriter.writeFormulaConstraint();
        codeWriter.writeNoSiphon();
        codeWriter.writeMarkedGraph();
        codeWriter.writePairwiseSum();
        codeWriter.writeOverApproximation1();
        codeWriter.writeOverApproximation2();
        for (int nbSegments = implementation.getParameters().getMinNumberOfSegments(); nbSegments <= implementation.getParameters().getMaxNumberOfSegments(); nbSegments++) {
            codeWriter.writeOverApproximation3(nbSegments);
        }
        for (int nbSegments = implementation.getParameters().getMinNumberOfSegments(); nbSegments <= implementation.getParameters().getMaxNumberOfSegments(); nbSegments++) {
            codeWriter.writeUnderApproximation(nbSegments);
        }
    }

    public void startChecking(ConfigurationModel configurationModel, ParametersModel parametersModel) {
        this.parametersModel = parametersModel;
        if (parametersModel.checkOverApproximation1()) {
            startChecking(ApproximationTypes.OVER_APPROXIMATION_1, configurationModel);
        } else if (parametersModel.checkOverApproximation2()) {
            startChecking(ApproximationTypes.OVER_APPROXIMATION_2, configurationModel);
        } else if (parametersModel.checkOverApproximation3()) {
            startChecking(ApproximationTypes.OVER_APPROXIMATION_3, configurationModel);
        } else if (parametersModel.checkUnderApproximation()) {
            startChecking(ApproximationTypes.UNDER_APPROXIMATION, configurationModel);
        }
    }

    private void startChecking(ApproximationTypes approximationType, IVerificationHandler verificationHandler) {
        switch (approximationType) {
            case OVER_APPROXIMATION_1:
                startOverApproximation1Checking(verificationHandler);
                break;
            case OVER_APPROXIMATION_2:
                startOverApproximation2Checking(verificationHandler);
                break;
            case OVER_APPROXIMATION_3:
                startOverApproximation3Checking(verificationHandler);
                break;
            case UNDER_APPROXIMATION:
                startUnderApproximationChecking(verificationHandler);
                break;
        }
    }

    public void setMinNumberOfSegments(int minNumberOfSegments) {
        this.minNumberOfSegments = minNumberOfSegments;
    }

    private void startOverApproximation1Checking(final IVerificationHandler verificationHandler) {
        (new SwingWorker<SingleSegmentApproximation, Void>() {
            @Override
            protected SingleSegmentApproximation doInBackground() throws Exception {
                return checkOverApproximation1();
            }

            @Override
            protected void done() {
                try {
                    SingleSegmentApproximation approximation = get();
                    verificationHandler.fireDoneChecking(ApproximationTypes.OVER_APPROXIMATION_1, approximation);
                    if (approximation.isSAT()) {
                        if (parametersModel.checkOverApproximation2()) {
                            startChecking(ApproximationTypes.OVER_APPROXIMATION_2, verificationHandler);
                        } else if (parametersModel.checkOverApproximation3()) {
                            startChecking(ApproximationTypes.OVER_APPROXIMATION_3, verificationHandler);
                        } else if (parametersModel.checkUnderApproximation()) {
                            startChecking(ApproximationTypes.UNDER_APPROXIMATION, verificationHandler);
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    private void startOverApproximation2Checking(final IVerificationHandler verificationHandler) {
        (new SwingWorker<SingleSegmentApproximation, Void>() {
            @Override
            protected SingleSegmentApproximation doInBackground() throws Exception {
                return checkOverApproximation2();
            }

            @Override
            protected void done() {
                try {
                    SingleSegmentApproximation approximation = get();
                    verificationHandler.fireDoneChecking(ApproximationTypes.OVER_APPROXIMATION_2, approximation);
                    if (approximation.isSAT() && !approximation.isValid()) {
                        setMinNumberOfSegments(approximation.getMaxValuation());
                        if (parametersModel.checkOverApproximation3()) {
                            startChecking(ApproximationTypes.OVER_APPROXIMATION_3, verificationHandler);
                        } else if (parametersModel.checkUnderApproximation()) {
                            startChecking(ApproximationTypes.UNDER_APPROXIMATION, verificationHandler);
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    private void startOverApproximation3Checking(final IVerificationHandler verificationHandler) {
        (new SwingWorker<MultipleSegmentsApproximation, Void>() {
            @Override
            protected MultipleSegmentsApproximation doInBackground() throws Exception {
                MultipleSegmentsApproximation approximation;
                for (int nbSegments = minNumberOfSegments; nbSegments < implementation.getParameters().getMaxNumberOfSegments(); nbSegments++) {
                    if ((approximation = checkOverApproximation3(nbSegments)).isSAT()) {
                        return approximation;
                    }
                }
                return checkOverApproximation3(implementation.getParameters().getMaxNumberOfSegments());
            }

            @Override
            protected void done() {
                try {
                    MultipleSegmentsApproximation approximation = get();
                    verificationHandler.fireDoneChecking(ApproximationTypes.OVER_APPROXIMATION_3, approximation);
                    if (approximation.isSAT()) {
                        if (parametersModel.checkUnderApproximation()) {
                            startChecking(ApproximationTypes.UNDER_APPROXIMATION, verificationHandler);
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    private void startUnderApproximationChecking(final IVerificationHandler verificationHandler) {
        (new SwingWorker<MultipleSegmentsApproximation, Void>() {
            @Override
            protected MultipleSegmentsApproximation doInBackground() throws Exception {
                MultipleSegmentsApproximation approximation;
                for (int nbSegments = minNumberOfSegments; nbSegments < implementation.getParameters().getMaxNumberOfSegments(); nbSegments++) {
                    if ((approximation = checkUnderApproximation(nbSegments)).isSAT()) {
                        return approximation;
                    }
                }
                return checkUnderApproximation(implementation.getParameters().getMaxNumberOfSegments());
            }

            @Override
            protected void done() {
                try {
                    MultipleSegmentsApproximation approximation = get();
                    verificationHandler.fireDoneChecking(ApproximationTypes.UNDER_APPROXIMATION, approximation);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    protected abstract SingleSegmentApproximation checkOverApproximation1();

    protected abstract SingleSegmentApproximation checkOverApproximation2();

    protected abstract MultipleSegmentsApproximation checkOverApproximation3(int nbSegments);

    protected abstract MultipleSegmentsApproximation checkUnderApproximation(int nbSegments);

}
