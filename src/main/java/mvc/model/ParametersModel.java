package mvc.model;

import mvc.eventsmanagement.IParametersListener;
import mvc.eventsmanagement.events.parameters.*;

import javax.swing.event.EventListenerList;

public class ParametersModel extends AbstractModel {

    private EventListenerList parametersListeners;
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
        parametersListeners = new EventListenerList();
        this.maxNodeValuation = maxNodeValuation;
        this.minNumberOfSegments = minNumberOfSegments;
        this.maxNumberOfSegments = maxNumberOfSegments;
        this.checkOverApproximation1 = checkOverApproximation1;
        this.checkOverApproximation2 = checkOverApproximation2;
        this.checkOverApproximation3 = checkOverApproximation3;
        this.checkUnderApproximation = checkUnderApproximation;
    }

    public void addParametersListener(IParametersListener parametersListener) {
        parametersListeners.add(IParametersListener.class, parametersListener);
    }

    public int getMaxNodeValuation() {
        return maxNodeValuation;
    }

    public void setMaxNodeValuation(int maxNodeValuation) {
        this.maxNodeValuation = maxNodeValuation;
        fireMaxNodeValuationChanged();
    }

    public void fireMaxNodeValuationChanged() {
        IParametersListener[] parametersListeners = this.parametersListeners.getListeners(IParametersListener.class);
        for (IParametersListener parameterListener : parametersListeners) {
            parameterListener.maxNodeValuationChanged(new MaxNodeValuationChanged(this, getMaxNodeValuation()));
        }
    }

    public int getMinNumberOfSegments() {
        return minNumberOfSegments;
    }

    public void setMinNumberOfSegments(int minNumberOfSegments) {
        this.minNumberOfSegments = minNumberOfSegments;
        fireMinNumberOfSegmentsChanged();
    }

    public void fireMinNumberOfSegmentsChanged() {
        IParametersListener[] parametersListeners = this.parametersListeners.getListeners(IParametersListener.class);
        for (IParametersListener parameterListener : parametersListeners) {
            parameterListener.minNumberOfSegmentsChanged(new MinNumberOfSegmentsChanged(this, getMinNumberOfSegments()));
        }
    }

    public int getMaxNumberOfSegments() {
        return maxNumberOfSegments;
    }

    public void setMaxNumberOfSegments(int maxNumberOfSegments) {
        this.maxNumberOfSegments = maxNumberOfSegments;
        fireMaxNumberOfSegmentsChanged();
    }

    private void fireMaxNumberOfSegmentsChanged() {
        IParametersListener[] parametersListeners = this.parametersListeners.getListeners(IParametersListener.class);
        for (IParametersListener parameterListener : parametersListeners) {
            parameterListener.maxNumberOfSegmentsChanged(new MaxNumberOfSegmentsChanged(this, getMaxNumberOfSegments()));
        }
    }

    public boolean checkOverApproximation1() {
        return checkOverApproximation1;
    }

    public void setCheckOverApproximation1(boolean check) {
        this.checkOverApproximation1 = check;
        fireCheckOverApproximation1Changed();
    }

    private void fireCheckOverApproximation1Changed() {
        IParametersListener[] parametersListeners = this.parametersListeners.getListeners(IParametersListener.class);
        for (IParametersListener parameterListener : parametersListeners) {
            parameterListener.checkOverApproximation1Changed(new CheckOverApproximation1Changed(this, checkOverApproximation1()));
        }
    }

    public boolean checkOverApproximation2() {
        return checkOverApproximation2;
    }

    public void setCheckOverApproximation2(boolean check) {
        this.checkOverApproximation2 = check;
        fireCheckOverApproximation2Changed();
    }

    private void fireCheckOverApproximation2Changed() {
        IParametersListener[] parametersListeners = this.parametersListeners.getListeners(IParametersListener.class);
        for (IParametersListener parameterListener : parametersListeners) {
            parameterListener.checkOverApproximation2Changed(new CheckOverApproximation2Changed(this, checkOverApproximation2()));
        }
    }

    public boolean checkOverApproximation3() {
        return checkOverApproximation3;
    }

    public void setCheckOverApproximation3(boolean check) {
        this.checkOverApproximation3 = check;
        fireCheckOverApproximation3Changed();
    }

    private void fireCheckOverApproximation3Changed() {
        IParametersListener[] parametersListeners = this.parametersListeners.getListeners(IParametersListener.class);
        for (IParametersListener parameterListener : parametersListeners) {
            parameterListener.checkOverApproximation3Changed(new CheckOverApproximation3Changed(this, checkOverApproximation3()));
        }
    }

    public boolean checkUnderApproximation() {
        return checkUnderApproximation;
    }

    public void setCheckUnderApproximation(boolean check) {
        this.checkUnderApproximation = check;
        fireCheckUnderApproximationChanged();
    }

    private void fireCheckUnderApproximationChanged() {
        IParametersListener[] parametersListeners = this.parametersListeners.getListeners(IParametersListener.class);
        for (IParametersListener parameterListener : parametersListeners) {
            parameterListener.checkUnderApproximationChanged(new CheckUnderApproximationChanged(this, checkUnderApproximation()));
        }
    }

}
