package codegeneration.implementations;

import petrinets.model.Workflow;
import specifications.model.Specification;

public abstract class Implementation {

    protected final Workflow workflow;
    protected final Specification specification;

    public Implementation(Workflow workflow, Specification specification) {
        this.workflow = workflow;
        this.specification = specification;
        init();
    }

    public abstract void init();

    public abstract Object getHeader();

    public abstract Object getInitialMarking();

    public abstract Object getFinalMarking();

    public abstract Object getStateEquation();

    public abstract Object getFormula();

    public abstract Object getNoSiphon();

    public abstract Object getMarkedGraph();

    public abstract Object getOverApproximation1();

    public abstract String getOverApproximation1Assertion();

    public abstract Object getOverApproximation2();

    public abstract String getOverApproximation2Assertion();

    public abstract Object getOverApproximation3();

    public abstract String getOverApproximation3Assertion();

    public abstract Object getUnderApproximation();

    public abstract String getUnderApproximationAssertion();

}
