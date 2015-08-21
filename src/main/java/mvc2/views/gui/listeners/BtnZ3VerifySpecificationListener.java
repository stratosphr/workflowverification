package mvc2.views.gui.listeners;

import mvc2.controller.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BtnZ3VerifySpecificationListener extends AbstractListener implements ActionListener {

    public BtnZ3VerifySpecificationListener(Controller controller) {
        super(controller);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        controller.notifyZ3VerificationRequired();
    }

}
