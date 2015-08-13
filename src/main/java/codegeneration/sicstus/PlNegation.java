package codegeneration.sicstus;

public class PlNegation extends PlPredicateCall {

    public PlNegation(PlBooleanExpr child) {
        super("\\+", child);
    }

}
