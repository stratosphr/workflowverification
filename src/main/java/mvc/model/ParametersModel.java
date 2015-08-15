package mvc.model;

public class ParametersModel extends AbstractModel {

    private int maxNodeValuation;
    private int minNumberOfSegments;
    private int maxNumberOfSegments;

    public void setMaxNodeValuation(int maxNodeValuation) {
        this.maxNodeValuation = maxNodeValuation;
    }

    public void setMinNumberOfSegments(int minNumberOfSegments) {
        this.minNumberOfSegments = minNumberOfSegments;
    }

    public void setMaxNumberOfSegments(int maxNumberOfSegments) {
        this.maxNumberOfSegments = maxNumberOfSegments;
    }

    public int getMaxNodeValuation() {
        return maxNodeValuation;
    }

    public int getMinNumberOfSegments() {
        return minNumberOfSegments;
    }

    public int getMaxNumberOfSegments() {
        return maxNumberOfSegments;
    }

}
