package mvc.eventsmanagement.events.configuration;

import codegeneration.implementations.z3.EZ3Implementation;
import mvc.controllers.AbstractController;
import mvc.controllers.ConfigurationController;
import mvc.views.gui.listeners.AbstractListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CbxZ3ImplementationListener extends AbstractListener implements ItemListener, ActionListener {

    public CbxZ3ImplementationListener(AbstractController controller) {
        super(controller);
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
            ((ConfigurationController) getController()).notifyZ3ImplementationChanged((EZ3Implementation) itemEvent.getItem());
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }

}
