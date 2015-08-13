package codegeneration.sicstus.fd;

import codegeneration.sicstus.PlPredicateCall;
import codegeneration.sicstus.PlTerm;

public class PlFDSum extends PlPredicateCall {

    public PlFDSum(String operator, PlTerm left, PlTerm right) {
        super("sum", left, new PlTerm(operator), right);
    }
}
