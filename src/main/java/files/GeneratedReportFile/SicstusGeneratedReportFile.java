package files.GeneratedReportFile;

import codegeneration.implementations.sicstus.ESicstusImplementation;
import files.SpecificationFile;
import files.VerificationFolder;

public class SicstusGeneratedReportFile extends GeneratedReportFile {

    public SicstusGeneratedReportFile(VerificationFolder verificationFolder, SpecificationFile specificationFile, ESicstusImplementation sicstusImplementation) {
        super(verificationFolder, "sicstus", sicstusImplementation.toString().toLowerCase(), specificationFile);
    }

}
