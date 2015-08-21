package files.GeneratedCodeFile;

import codegeneration.implementations.sicstus.ESicstusImplementations;
import files.SpecificationFile;
import files.VerificationFolder;

public class SicstusGeneratedCodeFile extends GeneratedCodeFile {

    public SicstusGeneratedCodeFile(VerificationFolder verificationFolder, SpecificationFile specificationFile, ESicstusImplementations sicstusImplementation) {
        super(verificationFolder, "sicstus", sicstusImplementation.toString().toLowerCase(), specificationFile, "pl");
    }

}
