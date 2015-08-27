package mvc.views.gui.listeners;

import mvc.controller.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BtnSicstusVerifySpecificationListener extends AbstractListener implements ActionListener {

    public BtnSicstusVerifySpecificationListener(Controller controller) {
        super(controller);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        controller.notifySicstusVerificationRequired();
    }

}
