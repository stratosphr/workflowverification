package codegeneration;

import codegeneration.implementations.AbstractImplementation;
import files.GeneratedCodeFile.GeneratedCodeFile;

public class CodeWriter extends SimpleWriter {

    private final AbstractImplementation implementation;

    public CodeWriter(GeneratedCodeFile generatedCodeFile, AbstractImplementation implementation) {
        super(generatedCodeFile);
        this.implementation = implementation;
    }

    public void writeStandardPredicates() {
        for (Object standardPredicate : implementation.getStandardPredicates()) {
            write(standardPredicate);
        }
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
