package files.GeneratedCodeFile;

import files.SpecificationFile;
import files.VerificationFolder;

import java.io.File;

public abstract class GeneratedCodeFile extends File {

    public GeneratedCodeFile(VerificationFolder verificationFolder, String subFolder, SpecificationFile specificationFile, String fileExtension) {
        super(verificationFolder.getAbsolutePath() + File.separator + "sources" + File.separator + subFolder + File.separator + specificationFile.extractSpecification().getName() + "." + fileExtension);
    }

}
