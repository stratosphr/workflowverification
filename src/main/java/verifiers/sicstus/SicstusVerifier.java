package verifiers.sicstus;

import codegeneration.implementations.AbstractImplementation;
import codegeneration.sicstus.PlTerm;
import files.GeneratedCodeFile.GeneratedCodeFile;
import reports.approximations.MultipleSegmentsApproximation;
import reports.approximations.SingleSegmentApproximation;
import verifiers.AbstractVerifier;

import java.util.HashMap;

public class SicstusVerifier extends AbstractVerifier {

    public SicstusVerifier(GeneratedCodeFile generatedCodeFile, AbstractImplementation implementation) {
        super(generatedCodeFile, implementation);
    }

    @Override
    protected SingleSegmentApproximation checkOverApproximation1() {
        Sicstus sicstus = Sicstus.getSingleton();
        HashMap<String, PlTerm> result = sicstus.query(generatedCodeFile, implementation.getOverApproximation1Assertion());
        return new SingleSegmentApproximation(result);
    }

    @Override
    protected SingleSegmentApproximation checkOverApproximation2() {
        Sicstus sicstus = Sicstus.getSingleton();
        HashMap<String, PlTerm> result = sicstus.query(generatedCodeFile, implementation.getOverApproximation2Assertion());
        return new SingleSegmentApproximation(result);
    }

    @Override
    protected MultipleSegmentsApproximation checkOverApproximation3(int nbSegments) {
        Sicstus sicstus = Sicstus.getSingleton();
        HashMap<String, PlTerm> result = sicstus.query(generatedCodeFile, implementation.getOverApproximation3Assertion(nbSegments));
        return new MultipleSegmentsApproximation(nbSegments, result);
    }

    @Override
    protected MultipleSegmentsApproximation checkUnderApproximation(int nbSegments) {
        Sicstus sicstus = Sicstus.getSingleton();
        HashMap<String, PlTerm> result = sicstus.query(generatedCodeFile, implementation.getUnderApproximationAssertion(nbSegments));
        return new MultipleSegmentsApproximation(nbSegments, result);
    }

}
