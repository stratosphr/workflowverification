package specifications.model.formulas;

public class NegationFormula extends CompoundFormula {

    public NegationFormula(Formula child) {
        super("not", child);
    }

}
