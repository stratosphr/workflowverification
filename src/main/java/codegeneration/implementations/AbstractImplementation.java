package codegeneration.implementations;

import mvc.models.ParametersModel;
import petrinets.model.Workflow;
import specifications.model.Specification;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class AbstractImplementation {

    protected final Workflow workflow;
    protected final Specification specification;
    private final ParametersModel parametersModel;

    public AbstractImplementation(Workflow workflow, Specification specification, ParametersModel parametersModel) {
        this.workflow = workflow;
        this.specification = specification;
        this.parametersModel = parametersModel;
        init();
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public Specification getSpecification() {
        return specification;
    }

    public ParametersModel getParametersModel() {
        return parametersModel;
    }

    public abstract void init();

    public ArrayList<Object> getStandardPredicates() {
        return new ArrayList<>(Arrays.asList(new Object[]{
                getHeader(),
                getInitialMarking(),
                getFinalMarking(),
                getStateEquation(),
                getFormula(),
                getNoSiphon(),
                getMarkedGraph()
        }));
    }

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

    public abstract Object getOverApproximation3(int nbSegments);

    public abstract String getOverApproximation3Assertion(int nbSegments);

    public abstract Object getUnderApproximation(int nbSegments);

    public abstract String getUnderApproximationAssertion(int nbSegments);

}
