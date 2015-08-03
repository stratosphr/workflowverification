package files.GeneratedReportFile;

import files.SpecificationFile;
import files.VerificationFolder;

import java.io.File;

public abstract class GeneratedReportFile extends File {

    public GeneratedReportFile(VerificationFolder verificationFolder, String subFolder, SpecificationFile specificationFile) {
        super(verificationFolder.getAbsolutePath() + File.separator + "reports" + File.separator + subFolder + File.separator + specificationFile.extractSpecification().getName() + ".xml");
    }

}
