package specifications.visitors;

public interface IVisitedFormula {

    void accept(IFormulaVisitor formulaVisitor);

}
