package files;

import specifications.SpecificationParser;
import specifications.model.Specification;

import java.io.File;

public class SpecificationFile extends File {

    public SpecificationFile(String path) {
        super(path);
    }

    public Specification extractSpecification() {
        return SpecificationParser.parse(this);
    }

    public boolean isValid() {
        return getAbsolutePath().endsWith(".xml");
    }

}
