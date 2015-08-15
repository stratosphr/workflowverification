package verifiers;

import codegeneration.CodeWriter;
import codegeneration.implementations.Implementation;
import files.GeneratedCodeFile.GeneratedCodeFile;
import reports.AbstractApproximation;
import reports.MultipleSegmentsApproximation;
import reports.SingleSegmentApproximation;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public abstract class AbstractVerifier implements IVerifier {

    protected final GeneratedCodeFile generatedCodeFile;
    protected final Implementation implementation;

    public AbstractVerifier(GeneratedCodeFile generatedCodeFile, Implementation implementation) {
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
        codeWriter.writeOverApproximation1();
        codeWriter.writeOverApproximation2();
        for (int nbSegments = implementation.getParameters().getMinNumberOfSegments(); nbSegments <= implementation.getParameters().getMaxNumberOfSegments(); nbSegments++) {
            codeWriter.writeOverApproximation3(nbSegments);
        }
        for (int nbSegments = implementation.getParameters().getMinNumberOfSegments(); nbSegments <= implementation.getParameters().getMaxNumberOfSegments(); nbSegments++) {
            codeWriter.writeUnderApproximation(nbSegments);
        }
    }

    public void startOverApproximation1Checking(final IVerificationHandler verificationHandler) {
        (new SwingWorker<AbstractApproximation, Void>() {
            @Override
            protected SingleSegmentApproximation doInBackground() throws Exception {
                return checkOverApproximation1();
            }

            @Override
            protected void done() {
                try {
                    verificationHandler.doneChecking(get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    public void startOverApproximation2Checking(final IVerificationHandler verificationHandler) {
        (new SwingWorker<AbstractApproximation, Void>() {
            @Override
            protected SingleSegmentApproximation doInBackground() throws Exception {
                return checkOverApproximation2();
            }

            @Override
            protected void done() {
                try {
                    verificationHandler.doneChecking(get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    public void startOverApproximation3Checking(final IVerificationHandler verificationHandler) {
        (new SwingWorker<AbstractApproximation, Void>() {
            @Override
            protected MultipleSegmentsApproximation doInBackground() throws Exception {
                MultipleSegmentsApproximation approximation;
                for (int nbSegments = implementation.getParameters().getMinNumberOfSegments(); nbSegments < implementation.getParameters().getMaxNumberOfSegments(); nbSegments++) {
                    if ((approximation = checkOverApproximation3(nbSegments)).isValid()) {
                        return approximation;
                    }
                }
                return checkOverApproximation3(implementation.getParameters().getMaxNumberOfSegments());
            }

            @Override
            protected void done() {
                try {
                    verificationHandler.doneChecking(get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    public void startUnderApproximationChecking(final IVerificationHandler verificationHandler) {
        (new SwingWorker<AbstractApproximation, Void>() {
            @Override
            protected MultipleSegmentsApproximation doInBackground() throws Exception {
                MultipleSegmentsApproximation approximation;
                for (int nbSegments = implementation.getParameters().getMinNumberOfSegments(); nbSegments < implementation.getParameters().getMaxNumberOfSegments(); nbSegments++) {
                    if ((approximation = checkUnderApproximation(nbSegments)).isValid()) {
                        return approximation;
                    }
                }
                return checkUnderApproximation(implementation.getParameters().getMaxNumberOfSegments());
            }

            @Override
            protected void done() {
                try {
                    verificationHandler.doneChecking(get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

}
