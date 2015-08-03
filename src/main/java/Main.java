import files.VerificationFolder;
import mvc.controllers.VerificationController;
import mvc.model.VerificationParameters;
import reports.Approximation;
import verifiers.IVerificationHandler;
import verifiers.sicstus.SicstusVerifier;
import verifiers.z3.Z3Verifier;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        setDefaultLookAndFeel("Nimbus");
        VerificationParameters verificationParameters = new VerificationParameters();
        VerificationController verificationController = new VerificationController(verificationParameters);
        verificationParameters.setVerificationFolder(new VerificationFolder("src/main/java/resources/mail"));
        verificationController.displayViews();
    }

    private static void setDefaultLookAndFeel(String lookAndFeelName) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (lookAndFeelName.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void example() {
        SicstusVerifier sicstusVerifier = new SicstusVerifier();
        Z3Verifier z3Verifier = new Z3Verifier();
        sicstusVerifier.startOverApproximation1Checking(new IVerificationHandler() {
            public void doneChecking(Approximation result) {
                System.out.println("Result from callback 1 : " + result);
            }
        });
        sicstusVerifier.startOverApproximation2Checking(new IVerificationHandler() {
            public void doneChecking(Approximation result) {
                System.out.println("Result from callback 2 : " + result);
            }
        });
        z3Verifier.startOverApproximation1Checking(new IVerificationHandler() {
            public void doneChecking(Approximation result) {
                System.out.println("Result from callback 1 : " + result);
            }
        });
        z3Verifier.startOverApproximation2Checking(new IVerificationHandler() {
            public void doneChecking(Approximation result) {
                System.out.println("Result from callback 2 : " + result);
            }
        });
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
