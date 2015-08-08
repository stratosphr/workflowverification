package codegeneration.sicstus.fd;

import codegeneration.sicstus.PlList;
import codegeneration.sicstus.PlPredicateCall;
import codegeneration.sicstus.PlTerm;

public class PlFDEquality extends PlPredicateCall{

    public PlFDEquality(PlList leftList, PlTerm rightVar) {
        super("sum", leftList, new PlTerm("#="), rightVar);
    }

}
