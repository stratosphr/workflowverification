package mvc.views.console;

import mvc.controllers.ConfigurationController;
import mvc.eventsmanagement.events.parameters.*;
import mvc.model.ParametersModel;
import mvc.views.AbstractParametersView;

public class ConsoleParametersView extends AbstractParametersView {

    private String representation;

    public ConsoleParametersView(ConfigurationController parametersController, ParametersModel parametersModel) {
        super(parametersController, parametersModel);
    }

    @Override
    public int getSpecifiedMaxNodeValuation() {
        return 0;
    }

    @Override
    public int getSpecifiedMinNumberOfSegments() {
        return 0;
    }

    @Override
    public int getSpecifiedMaxNumberOfSegments() {
        return 0;
    }

    @Override
    public boolean getCheckOverApproximation1isSelected() {
        return false;
    }

    @Override
    public boolean getCheckOverApproximation2isSelected() {
        return false;
    }

    @Override
    public boolean getCheckOverApproximation3isSelected() {
        return false;
    }

    @Override
    public boolean getCheckUnderApproximationisSelected() {
        return false;
    }

    @Override
    public void buildView() {
    }

    @Override
    public void display() {
        buildView();
    }

    @Override
    public void close() {
    }

    @Override
    public void maxNodeValuationChanged(MaxNodeValuationChanged event) {

    }

    @Override
    public void minNumberOfSegmentsChanged(MinNumberOfSegmentsChanged event) {

    }

    @Override
    public void maxNumberOfSegmentsChanged(MaxNumberOfSegmentsChanged event) {

    }

    @Override
    public void checkOverApproximation1Changed(CheckOverApproximation1Changed event) {

    }

    @Override
    public void checkOverApproximation2Changed(CheckOverApproximation2Changed event) {

    }

    @Override
    public void checkOverApproximation3Changed(CheckOverApproximation3Changed event) {

    }

    @Override
    public void checkUnderApproximationChanged(CheckUnderApproximationChanged event) {

    }

}
