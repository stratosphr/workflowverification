package mvc2.views.gui.listeners;

import codegeneration.implementations.z3.Z3Implementation;
import mvc2.controller.Controller;
import mvc2.views.gui.items.Z3ImplementationItem;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CbxZ3ImplementationListener extends AbstractListener implements ItemListener {

    public CbxZ3ImplementationListener(Controller controller) {
        super(controller);
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
           controller.notifyZ3ImplementationChanged(((Z3ImplementationItem) itemEvent.getItem()).getImplementation());
        }
    }

}
