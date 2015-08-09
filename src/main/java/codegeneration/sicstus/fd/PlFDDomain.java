package codegeneration.sicstus.fd;

import codegeneration.sicstus.PlPredicateCall;
import codegeneration.sicstus.PlTerm;

public class PlFDDomain extends PlPredicateCall {

    public PlFDDomain(PlTerm values, PlTerm inf, PlTerm sup) {
        super("domain", values, inf, sup);
    }

}
