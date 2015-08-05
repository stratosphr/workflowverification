package files.GeneratedCodeFile;

import files.SpecificationFile;
import files.VerificationFolder;

import java.io.File;

public abstract class GeneratedCodeFile extends File {

    public GeneratedCodeFile(VerificationFolder verificationFolder, String verifierSubFolder, String implementationSubFolder, SpecificationFile specificationFile, String fileExtension) {
        super(verificationFolder.getAbsolutePath() + File.separator + "sources" + File.separator + verifierSubFolder + File.separator + implementationSubFolder + File.separator + specificationFile.extractSpecification().getName() + "." + fileExtension);
    }

}
