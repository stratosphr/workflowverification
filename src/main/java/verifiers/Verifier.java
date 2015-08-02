package verifiers;

import reports.Approximation;
import reports.OverApproximation;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public abstract class Verifier implements IVerifier {

    public Verifier() {
    }

    public void startOverApproximation1Checking(final IVerificationHandler verificationHandler) {
        (new SwingWorker<Approximation, Void>() {
            @Override
            protected Approximation doInBackground() throws Exception {
                double i = 0;
                while (i < 100000) {
                    i += 0.0001;
                }
                return checkOverApproximation1();
            }

            @Override
            protected void done() {
                try {
                    verificationHandler.doneChecking(get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    public void startOverApproximation2Checking(final IVerificationHandler verificationHandler) {
        (new SwingWorker<Approximation, Void>() {
            @Override
            protected OverApproximation doInBackground() throws Exception {
                double i = 0;
                while (i < 100000) {
                    i += 0.00005;
                }

                return checkOverApproximation2();
            }

            @Override
            protected void done() {
                try {
                    verificationHandler.doneChecking(get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

}
