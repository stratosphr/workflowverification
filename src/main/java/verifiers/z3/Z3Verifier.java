package verifiers.z3;

import codegeneration.implementations.Implementation;
import codegeneration.z3.SMTTerm;
import files.GeneratedCodeFile.GeneratedCodeFile;
import reports.OverApproximation;
import verifiers.AbstractVerifier;

import java.util.HashMap;

public class Z3Verifier extends AbstractVerifier {

    public Z3Verifier(GeneratedCodeFile generatedCodeFile, Implementation implementation) {
        super(generatedCodeFile, implementation);
    }

    public OverApproximation checkOverApproximation1() {
        Z3 z3 = Z3.getSingleton();
        System.out.println(implementation.getOverApproximation1Assertion());
        HashMap<String, SMTTerm> result = z3.query(generatedCodeFile, implementation.getOverApproximation1Assertion());
        System.out.println("Result : " + result);
        return new OverApproximation();
    }

    public OverApproximation checkOverApproximation2() {
        return null;
    }

}
