package mvc.models;

import mvc.events.IParametersEventListener;
import mvc.events.events.ParametersChanged;

public class ParametersModel extends AbstractModel {

    private int maxNodeValuation;
    private int minNumberOfSegments;
    private int maxNumberOfSegments;
    private boolean checkOverApproximation1;
    private boolean checkOverApproximation2;
    private boolean checkOverApproximation3;
    private boolean checkUnderApproximation;

    public ParametersModel() {
        this(100, 2, 20, true, true, true, true);
    }

    public ParametersModel(int maxNodeValuation, int minNumberOfSegments, int maxNumberOfSegments, boolean checkOverApproximation1, boolean checkOverApproximation2, boolean checkOverApproximation3, boolean checkUnderApproximation) {
        super();
        this.maxNodeValuation = maxNodeValuation;
        this.minNumberOfSegments = minNumberOfSegments;
        this.maxNumberOfSegments = maxNumberOfSegments;
        this.checkOverApproximation1 = checkOverApproximation1;
        this.checkOverApproximation2 = checkOverApproximation2;
        this.checkOverApproximation3 = checkOverApproximation3;
        this.checkUnderApproximation = checkUnderApproximation;
    }

    public void addParametersListener(IParametersEventListener parametersListener) {
        eventListeners.add(IParametersEventListener.class, parametersListener);
    }

    public void update() {
        fireParametersChanged();
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

    public void setMaxNodeValuation(int maxNodeValuation) {
        this.maxNodeValuation = maxNodeValuation;
        update();
    }

    public void setMinNumberOfSegments(int minNumberOfSegments) {
        this.minNumberOfSegments = minNumberOfSegments;
        update();
    }

    public void setMaxNumberOfSegments(int maxNumberOfSegments) {
        this.maxNumberOfSegments = maxNumberOfSegments;
        update();
    }

    public void setCheckOverApproximation1(boolean checkOverApproximation1) {
        this.checkOverApproximation1 = checkOverApproximation1;
        update();
    }

    public void setCheckOverApproximation2(boolean checkOverApproximation2) {
        this.checkOverApproximation2 = checkOverApproximation2;
        update();
    }

    public void setCheckOverApproximation3(boolean checkOverApproximation3) {
        this.checkOverApproximation3 = checkOverApproximation3;
        update();
    }

    public void setCheckUnderApproximation(boolean checkUnderApproximation) {
        this.checkUnderApproximation = checkUnderApproximation;
        update();
    }

    /*****************************************************/
    /** FIRINGS ******************************************/
    /*****************************************************/

    private void fireParametersChanged() {
        for (IParametersEventListener parametersEventListener : eventListeners.getListeners(IParametersEventListener.class)) {
            parametersEventListener.parametersChanged(new ParametersChanged(this, maxNodeValuation, minNumberOfSegments, maxNumberOfSegments, checkOverApproximation1, checkOverApproximation2, checkOverApproximation3, checkUnderApproximation));
        }
    }

}
