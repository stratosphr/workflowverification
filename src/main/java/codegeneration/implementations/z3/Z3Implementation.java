package codegeneration.implementations.z3;

import codegeneration.implementations.Implementation;
import codegeneration.sicstus.PlPredicateDefinition;
import petrinets.model.Workflow;
import specifications.model.Specification;

public class Z3Implementation extends Implementation {

    public Z3Implementation(Workflow workflow, Specification specification) {
        super(workflow, specification);
    }

    @Override
    public void init() {

    }

    @Override
    public Object getHeader() {
        return null;
    }

    @Override
    public PlPredicateDefinition getInitialMarking() {
        return null;
    }

    @Override
    public Object getFinalMarking() {
        return null;
    }

    @Override
    public String getStateEquation() {
        return null;
    }

    @Override
    public String getFormula() {
        return null;
    }

    @Override
    public Object getNoSiphon() {
        return null;
    }

    @Override
    public Object[] getMarkedGraph() {
        return null;
    }

    @Override
    public String getOverApproximation1() {
        return null;
    }

    @Override
    public String getOverApproximation1Assertion() {
        return null;
    }

    @Override
    public String getOverApproximation2() {
        return null;
    }

    @Override
    public String getOverApproximation2Assertion() {
        return null;
    }

    @Override
    public String getOverApproximation3() {
        return null;
    }

    @Override
    public String getOverApproximation3Assertion() {
        return null;
    }

    @Override
    public String getUnderApproximation() {
        return null;
    }

    @Override
    public String getUnderApproximationAssertion() {
        return null;
    }

}
