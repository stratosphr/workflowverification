package mvc2.views.gui.listeners;

import mvc2.controller.Controller;

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
