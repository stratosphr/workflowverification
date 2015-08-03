package files;

import java.io.File;

public class GeneratedReportFile extends File {

    public GeneratedReportFile(VerificationFolder verificationFolder, SpecificationFile specificationFile) {
        super(verificationFolder.getAbsolutePath() + File.separator + "reports" + File.separator + specificationFile.getName());
    }

}
