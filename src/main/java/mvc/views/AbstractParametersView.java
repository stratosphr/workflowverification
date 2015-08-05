package mvc.views;

import mvc.controllers.ParametersController;
import mvc.eventsmanagement.IVerificationParametersListener;
import mvc.model.ParametersModel;

public abstract class AbstractParametersView extends AbstractView implements IVerificationParametersListener {

    public AbstractParametersView(ParametersController parametersController, ParametersModel parametersModel) {
        super(parametersController, parametersModel);
    }

    public ParametersController getParametersController() {
        return (ParametersController) getController();
    }

}
