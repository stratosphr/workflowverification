package specifications.model.formulas;

import java.util.ArrayList;

public class DisjunctionFormula extends CompoundFormula {

    public DisjunctionFormula(ArrayList<Formula> children) {
        super("or", children);
    }

    public DisjunctionFormula(Formula... children) {
        super("or", children);
    }

}
