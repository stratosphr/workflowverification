package mvc.views.gui.listeners.parameters;

import mvc.controllers.ParametersController;
import mvc.views.gui.listeners.AbstractListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BtnOkListener extends AbstractListener implements ActionListener {

    public BtnOkListener(ParametersController controller) {
        super(controller);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        ((ParametersController) getController()).notifyParametersEditionValidated();
    }

}
