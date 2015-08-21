package mvc2.views.gui.listeners;

import mvc2.controller.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BtnVerificationFolderListener extends AbstractListener implements ActionListener {

    public BtnVerificationFolderListener(Controller controller) {
        super(controller);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        controller.notifyEditVerificationFolderRequested();
    }

}
