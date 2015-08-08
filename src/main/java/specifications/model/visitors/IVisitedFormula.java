package specifications.model.visitors;

import codegeneration.sicstus.PlBooleanExpr;

public interface IVisitedFormula {

    PlBooleanExpr accept(IFormulaVisitor formulaVisitor);

}
