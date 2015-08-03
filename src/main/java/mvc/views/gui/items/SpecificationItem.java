package mvc.views.gui.items;

import files.SpecificationFile;
import specifications.model.Specification;

public class SpecificationItem {

    private final SpecificationFile specificationFile;
    private final Specification specification;

    public SpecificationItem(SpecificationFile specificationFile) {
        this.specificationFile = specificationFile;
        this.specification = specificationFile.extractSpecification();
    }

    public SpecificationFile getSpecificationFile() {
        return specificationFile;
    }

    @Override
    public String toString() {
        return specification.getName();
    }

}
