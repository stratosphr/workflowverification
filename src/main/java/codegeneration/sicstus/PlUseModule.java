package codegeneration.sicstus;

public class PlUseModule extends PlPredicateDefinition {

    public PlUseModule(PlLibrary clpfd) {
        super(":- use_module", clpfd);
    }

}
