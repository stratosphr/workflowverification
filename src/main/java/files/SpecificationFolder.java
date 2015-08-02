package files;

import java.io.File;

public class SpecificationFolder extends File {

    public SpecificationFolder(String path) {
        super(path);
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
                }
            }
        }
        return "specifications".equals(getName());
    }

}
