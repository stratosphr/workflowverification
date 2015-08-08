package codegeneration;

import codegeneration.implementations.Implementation;
import files.GeneratedCodeFile.GeneratedCodeFile;

public class CodeWriter extends SimpleWriter {

    private final Implementation implementation;

    public CodeWriter(GeneratedCodeFile generatedCodeFile, Implementation implementation) {
        super(generatedCodeFile);
        this.implementation = implementation;
    }

    public void writeStateEquation() {
        write(implementation.getStateEquation());
    }

    public void writeFormulaConstraint() {
        write(implementation.getFormulaConstraint());
    }

    public void writeOverApproximation1() {
        write(implementation.getOverApproximation1());
    }

}
