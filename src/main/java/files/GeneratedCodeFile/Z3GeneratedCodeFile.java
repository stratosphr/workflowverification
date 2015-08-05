package files.GeneratedCodeFile;

import codegeneration.implementations.z3.EZ3Implementation;
import files.SpecificationFile;
import files.VerificationFolder;

public class Z3GeneratedCodeFile extends GeneratedCodeFile {

    public Z3GeneratedCodeFile(VerificationFolder verificationFolder, SpecificationFile specificationFile, EZ3Implementation z3Implementation) {
        super(verificationFolder, "z3", z3Implementation.toString().toLowerCase(), specificationFile, "smt");
    }

}
