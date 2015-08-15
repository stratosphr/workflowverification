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
        codeWriter.writeOverApproximation3();
        codeWriter.writeUnderApproximation();
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
                return checkOverApproximation3();
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
                return checkUnderApproximation();
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
