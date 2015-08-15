package mvc.views.gui.listeners.parameters;

import mvc.controllers.ConfigurationController;
import mvc.views.gui.listeners.AbstractListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BtnCancelListener extends AbstractListener implements ActionListener {

    public BtnCancelListener(ConfigurationController configurationController) {
        super(configurationController);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        ((ConfigurationController) getController()).notifyParametersEditionAborted();
    }

}
