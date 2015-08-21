package verifiers.z3;

import codegeneration.implementations.AbstractImplementation;
import codegeneration.z3.SMTTerm;
import files.GeneratedCodeFile.Z3GeneratedCodeFile;
import reports.approximations.MultipleSegmentsApproximation;
import reports.approximations.SingleSegmentApproximation;
import verifiers.AbstractVerifier;

import java.util.HashMap;

public class Z3Verifier extends AbstractVerifier {

    public Z3Verifier(Z3GeneratedCodeFile generatedCodeFile, AbstractImplementation implementation) {
        super(generatedCodeFile, implementation);
    }

    @Override
    protected SingleSegmentApproximation checkOverApproximation1() {
        Z3 z3 = Z3.getSingleton();
        HashMap<String, SMTTerm> result = z3.query(generatedCodeFile, implementation.getOverApproximation1Assertion());
        return new SingleSegmentApproximation(result);
    }

    @Override
    protected SingleSegmentApproximation checkOverApproximation2() {
        Z3 z3 = Z3.getSingleton();
        HashMap<String, SMTTerm> result = z3.query(generatedCodeFile, implementation.getOverApproximation2Assertion());
        return new SingleSegmentApproximation(result);
    }

    @Override
    protected MultipleSegmentsApproximation checkOverApproximation3(int nbSegments) {
        Z3 z3 = Z3.getSingleton();
        HashMap<String, SMTTerm> result = z3.query(generatedCodeFile, implementation.getOverApproximation3Assertion(nbSegments));
        return new MultipleSegmentsApproximation(nbSegments, result);
    }

    @Override
    protected MultipleSegmentsApproximation checkUnderApproximation(int nbSegments) {
        Z3 z3 = Z3.getSingleton();
        HashMap<String, SMTTerm> result = z3.query(generatedCodeFile, implementation.getUnderApproximationAssertion(nbSegments));
        return new MultipleSegmentsApproximation(nbSegments, result);
    }

}
