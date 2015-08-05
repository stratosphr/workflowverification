package mvc.views.gui.listeners.parameters;

import mvc.controllers.ParametersController;
import mvc.views.gui.listeners.AbstractListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SpinMaxNumberOfSegmentsListener extends AbstractListener implements ChangeListener {

    public SpinMaxNumberOfSegmentsListener(ParametersController controller) {
        super(controller);
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        ((ParametersController) getController()).notifyMaxNumberOfSegmentsChanged((Integer) ((JSpinner) changeEvent.getSource()).getValue());
    }

}
