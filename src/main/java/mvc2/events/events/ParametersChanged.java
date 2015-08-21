package mvc2.events.events;

import mvc2.models.ParametersModel;

public class ParametersChanged extends AbstractEvent {

    private final int maxNodeValuation;
    private final int minNumberOfSegments;
    private final int maxNumberOfSegments;
    private final boolean checkOverApproximation1;
    private final boolean checkOverApproximation2;
    private final boolean checkOverApproximation3;
    private final boolean checkUnderApproximation;

    public ParametersChanged(ParametersModel source, int maxNodeValuation, int minNumberOfSegments, int maxNumberOfSegments, boolean checkOverApproximation1, boolean checkOverApproximation2, boolean checkOverApproximation3, boolean checkUnderApproximation) {
        super(source);
        this.maxNodeValuation = maxNodeValuation;
        this.minNumberOfSegments = minNumberOfSegments;
        this.maxNumberOfSegments = maxNumberOfSegments;
        this.checkOverApproximation1 = checkOverApproximation1;
        this.checkOverApproximation2 = checkOverApproximation2;
        this.checkOverApproximation3 = checkOverApproximation3;
        this.checkUnderApproximation = checkUnderApproximation;
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

    public boolean checkOverApproximation1() {
        return checkOverApproximation1;
    }

    public boolean checkOverApproximation2() {
        return checkOverApproximation2;
    }

    public boolean checkOverApproximation3() {
        return checkOverApproximation3;
    }

    public boolean checkUnderApproximation() {
        return checkUnderApproximation;
    }

}
