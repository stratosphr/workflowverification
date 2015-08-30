package mvc.views.gui.listeners;

import mvc.controller.Controller;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CbxSicstusSelectionHeuristicListener extends AbstractListener implements ItemListener {

    public CbxSicstusSelectionHeuristicListener(Controller controller) {
        super(controller);
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
            controller.notifySicstusSelectionHeuristicChanged(itemEvent.getItem().toString());
        }
    }
}
