package mvc2.views.gui.listeners;

import mvc2.controller.Controller;
import mvc2.views.gui.items.SicstusImplementationItem;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CbxSicstusImplementationListener extends AbstractListener implements ItemListener {

    public CbxSicstusImplementationListener(Controller controller) {
        super(controller);
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
           controller.notifySicstusImplementationChanged(((SicstusImplementationItem) itemEvent.getItem()).getImplementation());
        }
    }

}
