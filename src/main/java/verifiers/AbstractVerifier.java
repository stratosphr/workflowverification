package verifiers;

import codegeneration.CodeWriter;
import codegeneration.implementations.Implementation;
import files.GeneratedCodeFile.GeneratedCodeFile;
import reports.Approximation;
import reports.OverApproximation;

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
        codeWriter.writeOverApproximation1();
        codeWriter.writeOverApproximation2();
        codeWriter.writeOverApproximation3();
        codeWriter.writeUnderApproximation();
    }

    public void startOverApproximation1Checking(final IVerificationHandler verificationHandler) {
        (new SwingWorker<Approximation, Void>() {
            @Override
            protected Approximation doInBackground() throws Exception {
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
        (new SwingWorker<Approximation, Void>() {
            @Override
            protected OverApproximation doInBackground() throws Exception {
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

}
