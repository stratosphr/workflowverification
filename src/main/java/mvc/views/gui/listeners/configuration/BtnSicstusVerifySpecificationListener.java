package mvc.views.gui.listeners.configuration;

import mvc.controllers.ConfigurationController;
import mvc.views.gui.listeners.AbstractListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BtnSicstusVerifySpecificationListener extends AbstractListener implements ActionListener {

    public BtnSicstusVerifySpecificationListener(ConfigurationController configurationController) {
        super(configurationController);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        ((ConfigurationController) getController()).notifySicstusVerificationRequired();
    }

}
