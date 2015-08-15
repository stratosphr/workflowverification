package verifiers.z3;

import codegeneration.implementations.Implementation;
import codegeneration.z3.SMTTerm;
import files.GeneratedCodeFile.GeneratedCodeFile;
import reports.MultipleSegmentsApproximation;
import reports.SingleSegmentApproximation;
import verifiers.AbstractVerifier;

import java.util.HashMap;

public class Z3Verifier extends AbstractVerifier {

    public Z3Verifier(GeneratedCodeFile generatedCodeFile, Implementation implementation) {
        super(generatedCodeFile, implementation);
    }

    public SingleSegmentApproximation checkOverApproximation1() {
        Z3 z3 = Z3.getSingleton();
        HashMap<String, SMTTerm> result = z3.query(generatedCodeFile, implementation.getOverApproximation1Assertion());
        return new SingleSegmentApproximation(result);
    }

    public SingleSegmentApproximation checkOverApproximation2() {
        Z3 z3 = Z3.getSingleton();
        HashMap<String, SMTTerm> result = z3.query(generatedCodeFile, implementation.getOverApproximation2Assertion());
        return new SingleSegmentApproximation(result);
    }

    @Override
    public MultipleSegmentsApproximation checkOverApproximation3() {
        Z3 z3 = Z3.getSingleton();
        HashMap<String, SMTTerm> result = z3.query(generatedCodeFile, implementation.getOverApproximation3Assertion());
        return new MultipleSegmentsApproximation(result);
    }

    @Override
    public MultipleSegmentsApproximation checkUnderApproximation() {
        Z3 z3 = Z3.getSingleton();
        HashMap<String, SMTTerm> result = z3.query(generatedCodeFile, implementation.getUnderApproximationAssertion());
        return new MultipleSegmentsApproximation(result);
    }

}
