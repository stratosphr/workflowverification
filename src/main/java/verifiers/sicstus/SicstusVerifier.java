package verifiers.sicstus;

import codegeneration.implementations.Implementation;
import codegeneration.sicstus.PlTerm;
import files.GeneratedCodeFile.GeneratedCodeFile;
import reports.OverApproximation;
import verifiers.AbstractVerifier;

import java.util.HashMap;

public class SicstusVerifier extends AbstractVerifier {

    public SicstusVerifier(GeneratedCodeFile generatedCodeFile, Implementation implementation) {
        super(generatedCodeFile, implementation);
    }

    public OverApproximation checkOverApproximation1() {
        Sicstus sicstus = Sicstus.getSingleton();
        System.out.println(implementation.getOverApproximation1Assertion());
        HashMap<String, PlTerm> result = sicstus.query(generatedCodeFile, implementation.getOverApproximation1Assertion());
        System.out.println("Result : " + result);
        return new OverApproximation();
    }

    public OverApproximation checkOverApproximation2() {
        Sicstus sicstus = Sicstus.getSingleton();
        System.out.println(implementation.getOverApproximation2Assertion());
        HashMap<String, PlTerm> result = sicstus.query(generatedCodeFile, implementation.getOverApproximation2Assertion());
        System.out.println("Result : " + result);
        return new OverApproximation();
    }

    @Override
    public OverApproximation checkOverApproximation3() {
        Sicstus sicstus = Sicstus.getSingleton();
        System.out.println(implementation.getOverApproximation3Assertion());
        HashMap<String, PlTerm> result = sicstus.query(generatedCodeFile, implementation.getOverApproximation3Assertion());
        System.out.println("Result : " + result);
        return new OverApproximation();
    }

    @Override
    public OverApproximation checkUnderApproximation() {
        Sicstus sicstus = Sicstus.getSingleton();
        System.out.println(implementation.getUnderApproximationAssertion());
        HashMap<String, PlTerm> result = sicstus.query(generatedCodeFile, implementation.getUnderApproximationAssertion());
        System.out.println("Result : " + result);
        return new OverApproximation();
    }

}
