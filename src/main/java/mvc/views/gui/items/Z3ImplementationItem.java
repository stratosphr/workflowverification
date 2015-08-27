package mvc.views.gui.items;

import codegeneration.implementations.z3.EZ3Implementations;

public class Z3ImplementationItem extends AbstractItem {

    private EZ3Implementations implementation;

    public Z3ImplementationItem(EZ3Implementations implementation) {
        this.implementation = implementation;
    }

    public EZ3Implementations getImplementation() {
        return implementation;
    }

    @Override
    public String toString() {
        return implementation.toString();
    }

}
