package specifications.model.formulas;

import exceptions.NoChildrenCompoundFormulaException;
import tools.StringTools;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class CompoundFormula extends Formula {

    private ArrayList<Formula> children;

    public CompoundFormula(String name, ArrayList<Formula> children) {
        super(name);
        if (!children.isEmpty()) {
            this.children = children;
        } else {
            throw new NoChildrenCompoundFormulaException();
        }
    }

    public CompoundFormula(String name, Formula... children) {
        this(name, new ArrayList<>(Arrays.asList(children)));
    }

    @Override
    public String toString() {
        return name + "(" + StringTools.join(children, ", ") + ")";
    }

}
