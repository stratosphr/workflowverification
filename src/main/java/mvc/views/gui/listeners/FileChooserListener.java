package mvc.views.gui.listeners;

import files.VerificationFolder;
import mvc.controller.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileChooserListener extends AbstractListener implements ActionListener {

    public FileChooserListener(Controller controller) {
        super(controller);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (JFileChooser.APPROVE_SELECTION.equals(actionEvent.getActionCommand())) {
            controller.notifyVerificationFolderChanged(new VerificationFolder(((JFileChooser) actionEvent.getSource()).getSelectedFile().getAbsolutePath()));
        }
    }

}
