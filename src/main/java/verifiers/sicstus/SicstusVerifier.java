package verifiers.sicstus;

import codegeneration.implementations.Implementation;
import codegeneration.sicstus.PlTerm;
import files.GeneratedCodeFile.GeneratedCodeFile;
import reports.OverApproximation;
import verifiers.AbstractVerifier;

import java.io.File;
import java.util.HashMap;

public class SicstusVerifier extends AbstractVerifier {

    public SicstusVerifier(GeneratedCodeFile generatedCodeFile, Implementation implementation) {
        super(generatedCodeFile, implementation);
    }

    public OverApproximation checkOverApproximation1() {
        Sicstus sicstus = Sicstus.getSingleton();
        HashMap<String, PlTerm> result = sicstus.query(new File("src/main/resources/prolog_example_1.pl"), "example2(List)");
        System.out.println(result);
        return new OverApproximation();
    }

    public OverApproximation checkOverApproximation2() {
        return new OverApproximation();
    }

}
