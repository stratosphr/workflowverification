package mvc.views;

import mvc.controllers.ConfigurationController;
import mvc.eventsmanagement.IParametersListener;
import mvc.model.ParametersModel;

public abstract class AbstractParametersView extends AbstractView implements IParametersListener {

    public AbstractParametersView(ConfigurationController configurationController, ParametersModel parametersModel) {
        super(configurationController, parametersModel);
    }

    public abstract int getSpecifiedMaxNodeValuation();

    public abstract int getSpecifiedMinNumberOfSegments();

    public abstract int getSpecifiedMaxNumberOfSegments();

    public abstract boolean getCheckOverApproximation1isSelected();

    public abstract boolean getCheckOverApproximation2isSelected();

    public abstract boolean getCheckOverApproximation3isSelected();

    public abstract boolean getCheckUnderApproximationisSelected();

}
