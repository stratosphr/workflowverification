package mvc.model;

public class ParametersModel extends AbstractModel {

    private int maxNodeValuation;
    private int minNumberOfSegments;
    private int maxNumberOfSegments;

    public ParametersModel() {
        this(3000, 2, 3000);
    }

    public ParametersModel(int maxNodeValuation, int minNumberOfSegments, int maxNumberOfSegments) {
        this.maxNodeValuation = maxNodeValuation;
        this.minNumberOfSegments = minNumberOfSegments;
        this.maxNumberOfSegments = maxNumberOfSegments;
    }

    public void setMaxNodeValuation(int maxNodeValuation) {
        this.maxNodeValuation = maxNodeValuation;
    }

    public void setMinNumberOfSegments(int minNumberOfSegments) {
        this.minNumberOfSegments = minNumberOfSegments;
    }

    public void setMaxNumberOfSegments(int maxNumberOfSegments) {
        this.maxNumberOfSegments = maxNumberOfSegments;
    }

}
