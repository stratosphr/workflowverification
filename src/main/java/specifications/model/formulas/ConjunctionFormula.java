package specifications.model.formulas;

import java.util.ArrayList;

public class ConjunctionFormula extends CompoundFormula {

    public ConjunctionFormula(ArrayList<Formula> children) {
        super("and", children);
    }

    public ConjunctionFormula(Formula... children) {
        super("and", children);
    }

}
