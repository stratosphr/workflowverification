package mvc.views.gui.listeners.parameters;

import mvc.controllers.ParametersController;
import mvc.views.gui.listeners.AbstractListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BtnCancelListener extends AbstractListener implements ActionListener {

    public BtnCancelListener(ParametersController parametersController) {
        super(parametersController);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        ((ParametersController) getController()).notifyParametersEditionAborted();
    }

}
