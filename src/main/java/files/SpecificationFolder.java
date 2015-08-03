package files;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class SpecificationFolder extends File {

    private HashSet<SpecificationFile> specificationFiles;

    public SpecificationFolder(String path) {
        super(path);
        specificationFiles = new HashSet<>();
        isValid();
    }

    public boolean isValid() {
        File[] specifications = listFiles();
        if (specifications == null || specifications.length == 0) {
            return false;
        } else {
            for (File specification : specifications) {
                SpecificationFile specificationFile = new SpecificationFile(specification.getAbsolutePath());
                if (!specificationFile.isValid()) {
                    return false;
                } else {
                    specificationFiles.add(specificationFile);
                }
            }
        }
        return "specifications".equals(getName());
    }

    public ArrayList<SpecificationFile> getSpecificationFiles() {
        SpecificationFile[] specificationFiles = this.specificationFiles.toArray(new SpecificationFile[this.specificationFiles.size()]);
        Arrays.sort(specificationFiles);
        return new ArrayList<>(Arrays.asList(specificationFiles));
    }

}
