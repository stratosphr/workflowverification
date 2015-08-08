package specifications.model.visitors;

public interface IVisitedFormula {

    void accept(IFormulaVisitor formulaVisitor);

}
