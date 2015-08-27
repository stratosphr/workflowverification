package mvc.views.gui.items;

import codegeneration.implementations.sicstus.ESicstusImplementations;

public class SicstusImplementationItem extends AbstractItem {

    private final ESicstusImplementations implementation;

    public SicstusImplementationItem(ESicstusImplementations implementation) {
        this.implementation = implementation;
    }

    public ESicstusImplementations getImplementation() {
        return implementation;
    }

    @Override
    public String toString() {
        return implementation.toString();
    }

}
