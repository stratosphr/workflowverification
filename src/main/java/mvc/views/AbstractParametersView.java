package mvc.views;

import mvc.controllers.ConfigurationController;
import mvc.eventsmanagement.IParametersListener;
import mvc.model.ParametersModel;

public abstract class AbstractParametersView extends AbstractView implements IParametersListener {

    public AbstractParametersView(ConfigurationController configurationController, ParametersModel parametersModel) {
        super(configurationController, parametersModel);
    }

}
