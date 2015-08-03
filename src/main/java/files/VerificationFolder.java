package files;

import java.io.File;

public class VerificationFolder extends File {

    private SpecificationFolder specificationFolder;
    private PetriNetFile petriNetFile;

    public VerificationFolder(String path) {
        super(path);
        this.specificationFolder = getSpecificationFolder();
        this.petriNetFile = getPetriNetFile();
    }

    public boolean isValid() {
        System.err.println(petriNetFile);
        System.err.println(specificationFolder);
        return isDirectory() && petriNetFile != null && specificationFolder != null;
    }

    public PetriNetFile getPetriNetFile() {
        if (petriNetFile != null) {
            return petriNetFile;
        } else {
            File[] files = listFiles();
            if (files != null) {
                for (File file : files) {
                    PetriNetFile petriNetFile = new PetriNetFile(file.getAbsolutePath());
                    if (petriNetFile.isValid()) {
                        this.petriNetFile = petriNetFile;
                        break;
                    }
                }
            }
            return petriNetFile;
        }
    }

    public SpecificationFolder getSpecificationFolder() {
        if (specificationFolder != null) {
            return specificationFolder;
        } else {
            File[] files = listFiles();
            if (files == null || files.length == 0) {
                return null;
            } else {
                for (File file : files) {
                    SpecificationFolder specificationFolder = new SpecificationFolder(file.getAbsolutePath());
                    if (specificationFolder.isValid()) {
                        this.specificationFolder = specificationFolder;
                    }
                }
                return specificationFolder;
            }
        }
    }

}
