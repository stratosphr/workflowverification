package files;

import java.io.File;

public class GeneratedCodeFile extends File {

    public GeneratedCodeFile(VerificationFolder verificationFolder, SpecificationFile specificationFile) {
        super(verificationFolder.getAbsolutePath() + File.separator + "sources" + File.separator + specificationFile.getName());
    }

}
