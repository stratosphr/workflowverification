package mvc.views.gui.listeners.parameters;

import mvc.controllers.ConfigurationController;
import mvc.views.gui.listeners.AbstractListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SpinMinNumberOfSegmentsListener extends AbstractListener implements ChangeListener {

    public SpinMinNumberOfSegmentsListener(ConfigurationController configurationController) {
        super(configurationController);
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        ((ConfigurationController) getController()).notifyMinNumberOfSegmentsChanged((Integer) ((JSpinner) changeEvent.getSource()).getValue());
    }

}
