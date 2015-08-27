package mvc.views.gui.listeners;

import mvc.controller.Controller;

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
