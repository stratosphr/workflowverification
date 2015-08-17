package codegeneration.sicstus;

import java.util.ArrayList;
import java.util.Arrays;

public class PlSum extends PlCompoundBooleanExpr {

    public PlSum(PlTerm... parameters) {
        this(new ArrayList<PlTerm>(Arrays.asList(parameters)));
    }

    public PlSum(ArrayList<PlTerm> parameters) {
        super(" + ", parameters);
    }

}
