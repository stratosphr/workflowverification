package codegeneration.sicstus;

public class PlLibrary extends PlPredicateCall {

    public PlLibrary(String name) {
        super("library", new PlTerm(name));
    }

}
