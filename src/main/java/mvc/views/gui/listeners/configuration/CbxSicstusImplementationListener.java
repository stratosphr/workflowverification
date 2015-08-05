package mvc.views.gui.listeners.configuration;

import codegeneration.implementations.sicstus.ESicstusImplementation;
import mvc.controllers.ConfigurationController;
import mvc.views.gui.listeners.AbstractListener;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CbxSicstusImplementationListener extends AbstractListener implements ItemListener {

    public CbxSicstusImplementationListener(ConfigurationController configurationController) {
        super(configurationController);
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
            ((ConfigurationController) getController()).notifySicstusImplementationChanged((ESicstusImplementation) itemEvent.getItem());
        }
    }

}
