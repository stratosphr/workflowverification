package files;

import java.io.File;

public class SpecificationFile extends File {

    public SpecificationFile(String path) {
        super(path);
    }

    public boolean isValid() {
        return getAbsolutePath().endsWith(".xml");
    }

}
