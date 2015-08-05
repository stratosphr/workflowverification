package files.GeneratedReportFile;

import codegeneration.implementations.z3.EZ3Implementation;
import files.SpecificationFile;
import files.VerificationFolder;

public class Z3GeneratedReportFile extends GeneratedReportFile {

    public Z3GeneratedReportFile(VerificationFolder verificationFolder, SpecificationFile specificationFile, EZ3Implementation z3Implementation) {
        super(verificationFolder, "z3", z3Implementation.toString().toLowerCase(), specificationFile);
    }

}
