package mvc2.views.gui.listeners;

import mvc2.controller.Controller;
import mvc2.views.gui.items.SpecificationItem;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CbxSpecificationFileListener extends AbstractListener implements ItemListener {

    public CbxSpecificationFileListener(Controller controller) {
        super(controller);
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
            controller.notifySpecificationFileChanged(((SpecificationItem) itemEvent.getItem()).getSpecificationFile());
        }
    }

}
