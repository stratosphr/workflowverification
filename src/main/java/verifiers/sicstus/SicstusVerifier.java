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

    public SingleSegmentApproximation checkOverApproximation1() {
        Sicstus sicstus = Sicstus.getSingleton();
        HashMap<String, PlTerm> result = sicstus.query(generatedCodeFile, implementation.getOverApproximation1Assertion());
        return new SingleSegmentApproximation(result);
    }

    public SingleSegmentApproximation checkOverApproximation2() {
        Sicstus sicstus = Sicstus.getSingleton();
        HashMap<String, PlTerm> result = sicstus.query(generatedCodeFile, implementation.getOverApproximation2Assertion());
        return new SingleSegmentApproximation(result);
    }

    @Override
    public MultipleSegmentsApproximation checkOverApproximation3() {
        Sicstus sicstus = Sicstus.getSingleton();
        HashMap<String, PlTerm> result = sicstus.query(generatedCodeFile, implementation.getOverApproximation3Assertion());
        return new MultipleSegmentsApproximation(result);
    }

    @Override
    public MultipleSegmentsApproximation checkUnderApproximation() {
        Sicstus sicstus = Sicstus.getSingleton();
        HashMap<String, PlTerm> result = sicstus.query(generatedCodeFile, implementation.getUnderApproximationAssertion());
        return new MultipleSegmentsApproximation(result);
    }

}
