package codegeneration.implementations;

import mvc.model.ParametersModel;
import petrinets.model.Workflow;
import specifications.model.Specification;

public abstract class Implementation {

    protected final Workflow workflow;
    protected final Specification specification;
    private final ParametersModel parametersModel;

    public Implementation(Workflow workflow, Specification specification, ParametersModel parametersModel) {
        this.workflow = workflow;
        this.specification = specification;
        this.parametersModel = parametersModel;
        init();
    }

    public ParametersModel getParameters(){
        return parametersModel;
    }

    public abstract void init();

    public abstract Object getHeader();

    public abstract Object getInitialMarking();

    public abstract Object getFinalMarking();

    public abstract Object getStateEquation();

    public abstract Object getFormula();

    public abstract Object getNoSiphon();

    public abstract Object getMarkedGraph();

    public abstract Object getPairwiseSum();

    public abstract Object getOverApproximation1();

    public abstract String getOverApproximation1Assertion();

    public abstract Object getOverApproximation2();

    public abstract String getOverApproximation2Assertion();

    public abstract Object getOverApproximation3(int nbSegments);

    public abstract String getOverApproximation3Assertion(int nbSegments);

    public abstract Object getUnderApproximation(int nbSegments);

    public abstract String getUnderApproximationAssertion(int nbSegments);

}
