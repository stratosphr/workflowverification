package files.GeneratedReportFile;

import codegeneration.implementations.sicstus.ESicstusImplementations;
import files.SpecificationFile;
import files.VerificationFolder;

public class SicstusGeneratedReportFile extends GeneratedReportFile {

    public SicstusGeneratedReportFile(VerificationFolder verificationFolder, SpecificationFile specificationFile, ESicstusImplementations sicstusImplementation) {
        super(verificationFolder, "sicstus", sicstusImplementation.toString().toLowerCase(), specificationFile);
    }

}
