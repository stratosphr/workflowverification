package files.GeneratedCodeFile;

import codegeneration.implementations.sicstus.ESicstusImplementation;
import files.SpecificationFile;
import files.VerificationFolder;

public class SicstusGeneratedCodeFile extends GeneratedCodeFile {

    public SicstusGeneratedCodeFile(VerificationFolder verificationFolder, SpecificationFile specificationFile, ESicstusImplementation sicstusImplementation) {
        super(verificationFolder, "sicstus", sicstusImplementation.toString().toLowerCase(), specificationFile, "pl");
    }

}
