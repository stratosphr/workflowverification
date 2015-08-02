import codegeneration.xml.XMLDocument;
import codegeneration.xml.XMLNode;
import codegeneration.xml.XMLTextNode;
import petrinets.PIPEParser;
import reports.Approximation;
import verifiers.IVerificationHandler;
import verifiers.sicstus.SicstusVerifier;
import verifiers.z3.Z3Verifier;

import javax.swing.*;
import java.io.File;

public class Main {

    public static void main(String[] args) {

        XMLDocument document = new XMLDocument();
        XMLNode root = new XMLNode("root");
        XMLNode parent1 = new XMLNode("parent1");
        parent1.addChild(new XMLTextNode("text1", "value1"));
        XMLNode parent2 = new XMLNode("parent2");
        parent2.addChild(new XMLTextNode("text1", "value1"));
        parent2.addChild(new XMLTextNode("text2", "value2"));
        XMLNode parent3 = new XMLNode("parent3");
        parent3.addChild(new XMLTextNode("text1", "value1"));
        parent3.addChild(new XMLTextNode("text2", "value2"));
        parent3.addChild(new XMLTextNode("text3", "value3"));
        root.addChild(parent1);
        root.addChild(parent2);
        root.addChild(parent3);
        document.setRootNode(root);
        //System.out.println(document);
        File workflow;
        if ((new File("resources/verificationFolderExample2/mail.xml")).exists()) {
            workflow = new File("resources/verificationFolderExample2/mail.xml");
        } else {
            workflow = new File("src/main/resources/verificationFolderExample2/mail.xml");
        }
        PIPEParser.parse(workflow);

        /*setDefaultLookAndFeel("Nimbus");
        VerificationFolder verificationFolder;
        if ((new File("resources/verificationFolderExample")).exists()) {
            verificationFolder = new VerificationFolder("resources/verificationFolderExample");
        } else {
            verificationFolder = new VerificationFolder("src/main/resources/verificationFolderExample");
        }
        PetriNet petriNet = new PetriNet();
        Place p1 = new Place("p1");
        Place p2 = new Place("p2");
        Transition t1 = new Transition("t1");
        Transition t2 = new Transition("t2");
        petriNet.addFlow(new PTFlow(p1, t1));
        petriNet.addFlow(new PTFlow(p1, t2));
        petriNet.addFlow(new TPFlow(t2, p1));
        petriNet.addFlow(new TPFlow(t2, p2));
        petriNet.addFlow(new PTFlow(p2, t2));
        System.out.println(petriNet);
        MainWindow.main(verificationFolder);
        System.out.println(verificationFolder.getPetriNetFile().extractPetriNet());*/
        //MainWindow.main();
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
