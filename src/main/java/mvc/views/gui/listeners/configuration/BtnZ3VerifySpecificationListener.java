package mvc.views.gui.listeners.configuration;

import mvc.controllers.ConfigurationController;
import mvc.views.gui.listeners.AbstractListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BtnZ3VerifySpecificationListener extends AbstractListener implements ActionListener {

    public BtnZ3VerifySpecificationListener(ConfigurationController configurationController) {
        super(configurationController);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        ((ConfigurationController)getController()).notifyZ3VerificationRequired();
    }

}
