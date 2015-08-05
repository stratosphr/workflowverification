package mvc.views.gui.listeners.configuration;

import mvc.controllers.ConfigurationController;
import mvc.views.gui.items.SpecificationItem;
import mvc.views.gui.listeners.AbstractListener;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CbxSpecificationFileListener extends AbstractListener implements ItemListener {

    public CbxSpecificationFileListener(ConfigurationController configurationController) {
        super(configurationController);
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
            ((ConfigurationController) getController()).notifySpecificationFileChanged(((SpecificationItem) itemEvent.getItem()).getSpecificationFile());
        }
    }

}
