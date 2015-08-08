package specifications.model.formulas;

import codegeneration.sicstus.PlBooleanExpr;
import exceptions.NoChildrenCompoundFormulaException;
import petrinets.model.Transition;
import petrinets.model.Workflow;
import specifications.model.visitors.IFormulaVisitor;
import specifications.model.visitors.IVisitedFormula;
import tools.StringTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

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
    public LinkedHashSet<Transition> getUsedTransitions(Workflow workflow) {
        LinkedHashSet<Transition> usedTransitions = new LinkedHashSet<>();
        for (Formula child : children) {
            usedTransitions.addAll(child.getUsedTransitions(workflow));
        }
        return usedTransitions;
    }

    @Override
    public String toString() {
        return name + "(" + StringTools.join(children, ", ") + ")";
    }

    @Override
    public PlBooleanExpr accept(IFormulaVisitor formulaVisitor) {
        ArrayList<PlBooleanExpr> subConstraints = new ArrayList<>();
        for (IVisitedFormula child : children) {
            subConstraints.add(child.accept(formulaVisitor));
        }
        return null;
    }

}
