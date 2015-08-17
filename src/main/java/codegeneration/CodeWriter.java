package codegeneration;

import codegeneration.implementations.Implementation;
import files.GeneratedCodeFile.GeneratedCodeFile;

public class CodeWriter extends SimpleWriter {

    private final Implementation implementation;

    public CodeWriter(GeneratedCodeFile generatedCodeFile, Implementation implementation) {
        super(generatedCodeFile);
        this.implementation = implementation;
    }

    public void writeHeader(){
        write(implementation.getHeader());
    }

    public void writeInitialMarking() {
        write(implementation.getInitialMarking());
    }

    public void writeFinalMarking() {
        write(implementation.getFinalMarking());
    }

    public void writeStateEquation() {
        write(implementation.getStateEquation());
    }

    public void writeFormulaConstraint() {
        write(implementation.getFormula());
    }

    public void writeNoSiphon(){
        write(implementation.getNoSiphon());
    }

    public void writeMarkedGraph(){
        write(implementation.getMarkedGraph());
    }

    public void writePairwiseSum(){
        write(implementation.getPairwiseSum());
    }

    public void writeOverApproximation1() {
        write(implementation.getOverApproximation1());
    }

    public void writeOverApproximation2() {
        write(implementation.getOverApproximation2());
    }

    public void writeOverApproximation3(int nbSegments) {
        write(implementation.getOverApproximation3(nbSegments));
    }

    public void writeUnderApproximation(int nbSegments) {
        write(implementation.getUnderApproximation(nbSegments));
    }

}
