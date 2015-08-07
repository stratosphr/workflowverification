package codegeneration.implementations.sicstus;

import codegeneration.implementations.Implementation;
import codegeneration.sicstus.PlPredicateDefinition;
import petrinets.model.Workflow;
import specifications.model.Specification;

public class SicstusImplementation extends Implementation {

    public SicstusImplementation(Workflow workflow, Specification specification) {
        super(workflow, specification);
    }

    @Override
    public String getStateEquation() {
        return new PlPredicateDefinition("overApproximation").toString();
    }

    @Override
    public String getFormulaConstraint() {
        return new PlPredicateDefinition("formula").toString();
    }

}
