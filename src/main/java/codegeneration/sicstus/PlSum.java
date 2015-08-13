package codegeneration.sicstus;

import java.util.ArrayList;

public class PlSum extends PlCompoundBooleanExpr {

    public PlSum(ArrayList<PlTerm> parameters) {
        super(" + ", parameters);
    }

}
