package verifiers;

import codegeneration.CodeWriter;
import codegeneration.implementations.AbstractImplementation;
import files.GeneratedCodeFile.GeneratedCodeFile;
import mvc2.models.VerificationModel;
import mvc2.models.ParametersModel;
import reports.Report;
import reports.approximations.ApproximationTypes;
import reports.approximations.MultipleSegmentsApproximation;
import reports.approximations.SingleSegmentApproximation;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public abstract class AbstractVerifier {

    protected final GeneratedCodeFile generatedCodeFile;
    protected final AbstractImplementation implementation;
    private final CodeWriter codeWriter;
    private ParametersModel parametersModel;
    private int minNumberOfSegments;

    public AbstractVerifier(GeneratedCodeFile generatedCodeFile, AbstractImplementation implementation) {
        this.generatedCodeFile = generatedCodeFile;
        this.implementation = implementation;
        this.codeWriter = new CodeWriter(generatedCodeFile, implementation);
    }

    public void startChecking(VerificationModel verificationModel, ParametersModel parametersModel) {
        this.parametersModel = parametersModel;
        codeWriter.writeHeader();
        codeWriter.writeInitialMarking();
        codeWriter.writeFinalMarking();
        codeWriter.writeStateEquation();
        codeWriter.writeFormulaConstraint();
        codeWriter.writeNoSiphon();
        codeWriter.writeMarkedGraph();
        codeWriter.writePairwiseSum();
        if (parametersModel.checkOverApproximation1()) {
            startChecking(ApproximationTypes.OVER_APPROXIMATION_1, verificationModel);
        } else if (parametersModel.checkOverApproximation2()) {
            startChecking(ApproximationTypes.OVER_APPROXIMATION_2, verificationModel);
        } else if (parametersModel.checkOverApproximation3()) {
            startChecking(ApproximationTypes.OVER_APPROXIMATION_3, verificationModel);
        } else if (parametersModel.checkUnderApproximation()) {
            startChecking(ApproximationTypes.UNDER_APPROXIMATION, verificationModel);
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
                verificationHandler.fireWritingStarted(implementation.getSpecification().getType(), ApproximationTypes.OVER_APPROXIMATION_1);
                codeWriter.writeOverApproximation1();
                verificationHandler.fireWritingDone(implementation.getSpecification().getType(), ApproximationTypes.OVER_APPROXIMATION_1);
                verificationHandler.fireCheckingStarted(implementation.getSpecification().getType(), ApproximationTypes.OVER_APPROXIMATION_1);
                return checkOverApproximation1();
            }

            @Override
            protected void done() {
                try {
                    SingleSegmentApproximation approximation = get();
                    verificationHandler.fireCheckingDone(new Report(implementation, ApproximationTypes.OVER_APPROXIMATION_1, approximation));
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
                verificationHandler.fireWritingStarted(implementation.getSpecification().getType(), ApproximationTypes.OVER_APPROXIMATION_2);
                codeWriter.writeOverApproximation2();
                verificationHandler.fireWritingDone(implementation.getSpecification().getType(), ApproximationTypes.OVER_APPROXIMATION_2);
                verificationHandler.fireCheckingStarted(implementation.getSpecification().getType(), ApproximationTypes.OVER_APPROXIMATION_2);
                return checkOverApproximation2();
            }

            @Override
            protected void done() {
                try {
                    SingleSegmentApproximation approximation = get();
                    verificationHandler.fireCheckingDone(new Report(implementation, ApproximationTypes.OVER_APPROXIMATION_2, approximation));
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
                for (int segment = minNumberOfSegments; segment < implementation.getParametersModel().getMaxNumberOfSegments(); segment++) {
                    verificationHandler.fireWritingStarted(implementation.getSpecification().getType(), ApproximationTypes.OVER_APPROXIMATION_3, segment);
                    codeWriter.writeOverApproximation3(segment);
                    verificationHandler.fireWritingDone(implementation.getSpecification().getType(), ApproximationTypes.OVER_APPROXIMATION_3, segment);
                    verificationHandler.fireCheckingStarted(implementation.getSpecification().getType(), ApproximationTypes.OVER_APPROXIMATION_3, segment);
                    if ((approximation = checkOverApproximation3(segment)).isSAT()) {
                        return approximation;
                    }
                }
                return checkOverApproximation3(implementation.getParametersModel().getMaxNumberOfSegments());
            }

            @Override
            protected void done() {
                try {
                    MultipleSegmentsApproximation approximation = get();
                    verificationHandler.fireCheckingDone(new Report(implementation, ApproximationTypes.OVER_APPROXIMATION_3, approximation));
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
                for (int segment = minNumberOfSegments; segment < implementation.getParametersModel().getMaxNumberOfSegments(); segment++) {
                    verificationHandler.fireWritingStarted(implementation.getSpecification().getType(), ApproximationTypes.UNDER_APPROXIMATION, segment);
                    codeWriter.writeUnderApproximation(segment);
                    verificationHandler.fireWritingDone(implementation.getSpecification().getType(), ApproximationTypes.UNDER_APPROXIMATION, segment);
                    verificationHandler.fireCheckingStarted(implementation.getSpecification().getType(), ApproximationTypes.UNDER_APPROXIMATION, segment);
                    if ((approximation = checkUnderApproximation(segment)).isSAT()) {
                        return approximation;
                    }
                }
                return checkUnderApproximation(implementation.getParametersModel().getMaxNumberOfSegments());
            }

            @Override
            protected void done() {
                try {
                    MultipleSegmentsApproximation approximation = get();
                    verificationHandler.fireCheckingDone(new Report(implementation, ApproximationTypes.UNDER_APPROXIMATION, approximation));
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
