package verifiers.sicstus;

import codegeneration.implementations.Implementation;
import codegeneration.sicstus.PlTerm;
import files.GeneratedCodeFile.GeneratedCodeFile;
import reports.MultipleSegmentsApproximation;
import reports.SingleSegmentApproximation;
import verifiers.AbstractVerifier;

import java.util.HashMap;

public class SicstusVerifier extends AbstractVerifier {

    public SicstusVerifier(GeneratedCodeFile generatedCodeFile, Implementation implementation) {
        super(generatedCodeFile, implementation);
    }

    @Override
    public SingleSegmentApproximation checkOverApproximation1() {
        Sicstus sicstus = Sicstus.getSingleton();
        HashMap<String, PlTerm> result = sicstus.query(generatedCodeFile, implementation.getOverApproximation1Assertion());
        return new SingleSegmentApproximation(result);
    }

    @Override
    public SingleSegmentApproximation checkOverApproximation2() {
        Sicstus sicstus = Sicstus.getSingleton();
        HashMap<String, PlTerm> result = sicstus.query(generatedCodeFile, implementation.getOverApproximation2Assertion());
        return new SingleSegmentApproximation(result);
    }

    @Override
    public MultipleSegmentsApproximation checkOverApproximation3(int nbSegments) {
        Sicstus sicstus = Sicstus.getSingleton();
        HashMap<String, PlTerm> result = sicstus.query(generatedCodeFile, implementation.getOverApproximation3Assertion(nbSegments));
        return new MultipleSegmentsApproximation(result);
    }

    @Override
    public MultipleSegmentsApproximation checkUnderApproximation(int nbSegments) {
        Sicstus sicstus = Sicstus.getSingleton();
        HashMap<String, PlTerm> result = sicstus.query(generatedCodeFile, implementation.getUnderApproximationAssertion(nbSegments));
        return new MultipleSegmentsApproximation(result);
    }

}
