package reports;

import files.WorkflowFile;
import mvc.model.ConfigurationModel;
import mvc.model.ParametersModel;
import petrinets.model.Workflow;
import specifications.model.Specification;
import tools.StringTools;

public class Report {

    private final ConfigurationModel configurationModel;
    private final ParametersModel parametersModel;
    private final AbstractApproximation approximation;

    public Report(ConfigurationModel configurationModel, ParametersModel parametersModel, AbstractApproximation approximation) {
        this.configurationModel = configurationModel;
        this.parametersModel = parametersModel;
        this.approximation = approximation;
    }

    public WorkflowFile getWorkflowFile() {
        return configurationModel.getVerificationFolder().getWorkflowFile();
    }

    public Workflow getWorkflow() {
        return getWorkflowFile().extractWorkflow();
    }

    public Specification getSpecification() {
        return configurationModel.getSpecificationFile().extractSpecification();
    }

    private AbstractApproximation getApproximation() {
        return approximation;
    }

    private String getHeaderStr() {
        String str = "";
        str += "Workflow : \n\t" + getWorkflowFile().getName() + "\n";
        str += getSpecification();
        return str;
    }

    @Override
    public String toString() {
        String str = "";
        int separatorSize = 50;
        str += StringTools.separator(separatorSize);
        str += getHeaderStr();
        str += StringTools.separator(separatorSize);
        str += getApproximation();
        return str;
    }

}
