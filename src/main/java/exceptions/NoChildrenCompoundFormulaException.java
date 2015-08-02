package exceptions;

public class NoChildrenCompoundFormulaException extends RuntimeException {

    public NoChildrenCompoundFormulaException() {
        super("Instantiating a compound formula with no children is forbidden.");
    }

}
