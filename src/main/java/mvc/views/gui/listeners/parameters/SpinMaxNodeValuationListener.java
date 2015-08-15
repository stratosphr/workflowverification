package mvc.views.gui.listeners.parameters;

import mvc.controllers.ConfigurationController;
import mvc.views.gui.listeners.AbstractListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SpinMaxNodeValuationListener extends AbstractListener implements ChangeListener {

    public SpinMaxNodeValuationListener(ConfigurationController configurationController) {
        super(configurationController);
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        ((ConfigurationController) getController()).notifyMaxNodeValuationChanged((Integer) ((JSpinner) changeEvent.getSource()).getValue());
    }

}
