package mvc.views.gui.listeners;

import mvc.controller.Controller;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CbxSicstusChoicesHeuristicListener extends AbstractListener implements ItemListener {

    public CbxSicstusChoicesHeuristicListener(Controller controller) {
        super(controller);
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
            controller.notifySicstusChoicesHeuristicChanged(itemEvent.getItem().toString());
        }
    }

}
