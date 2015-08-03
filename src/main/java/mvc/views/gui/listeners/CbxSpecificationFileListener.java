package mvc.views.gui.listeners;

import mvc.controllers.VerificationController;
import mvc.views.gui.items.SpecificationItem;

import java.awt.event.ItemEvent;

public class CbxSpecificationFileListener implements java.awt.event.ItemListener {

    private VerificationController verificationController;

    public CbxSpecificationFileListener(VerificationController verificationController) {
        this.verificationController = verificationController;
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if(itemEvent.getStateChange() == ItemEvent.SELECTED){
            verificationController.notifySpecificationFileChanged(((SpecificationItem) itemEvent.getItem()).getSpecificationFile());
        }
    }

}
