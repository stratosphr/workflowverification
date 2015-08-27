package mvc.views.gui.listeners;

import mvc.controller.Controller;
import mvc.views.gui.items.SpecificationItem;

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
