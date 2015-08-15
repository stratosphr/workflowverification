package verifiers.z3;

import codegeneration.implementations.Implementation;
import codegeneration.z3.SMTTerm;
import files.GeneratedCodeFile.Z3GeneratedCodeFile;
import reports.MultipleSegmentsApproximation;
import reports.SingleSegmentApproximation;
import verifiers.AbstractVerifier;

import java.util.HashMap;

public class Z3Verifier extends AbstractVerifier {

    public Z3Verifier(Z3GeneratedCodeFile generatedCodeFile, Implementation implementation) {
        super(generatedCodeFile, implementation);
    }

    @Override
    public SingleSegmentApproximation checkOverApproximation1() {
        Z3 z3 = Z3.getSingleton();
        HashMap<String, SMTTerm> result = z3.query(generatedCodeFile, implementation.getOverApproximation1Assertion());
        return new SingleSegmentApproximation();
    }

    @Override
    public SingleSegmentApproximation checkOverApproximation2() {
        Z3 z3 = Z3.getSingleton();
        HashMap<String, SMTTerm> result = z3.query(generatedCodeFile, implementation.getOverApproximation2Assertion());
        return new SingleSegmentApproximation();
    }

    @Override
    public MultipleSegmentsApproximation checkOverApproximation3(int nbSegments) {
        Z3 z3 = Z3.getSingleton();
        HashMap<String, SMTTerm> result = z3.query(generatedCodeFile, implementation.getOverApproximation3Assertion(nbSegments));
        return new MultipleSegmentsApproximation();
    }

    @Override
    public MultipleSegmentsApproximation checkUnderApproximation(int nbSegments) {
        Z3 z3 = Z3.getSingleton();
        HashMap<String, SMTTerm> result = z3.query(generatedCodeFile, implementation.getUnderApproximationAssertion(nbSegments));
        return new MultipleSegmentsApproximation();
    }

}
