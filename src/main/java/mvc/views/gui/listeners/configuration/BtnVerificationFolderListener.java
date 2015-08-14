package mvc.views.gui.listeners.configuration;

import files.VerificationFolder;
import mvc.controllers.ConfigurationController;
import mvc.model.ConfigurationModel;
import mvc.views.gui.listeners.AbstractListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BtnVerificationFolderListener extends AbstractListener implements ActionListener, MouseListener {

    public BtnVerificationFolderListener(ConfigurationController configurationController) {
        super(configurationController);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JFileChooser verificationFolderChooser = new JFileChooser();
        verificationFolderChooser.setDialogTitle("Select a verification folder");
        verificationFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        ConfigurationModel configurationModel = ((ConfigurationController) getController()).getModel();
        verificationFolderChooser.setCurrentDirectory(configurationModel.getVerificationFolder().getParentFile());
        if (verificationFolderChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            ((ConfigurationController) getController()).notifyVerificationFolderChanged(new VerificationFolder(verificationFolderChooser.getSelectedFile().getAbsolutePath()));
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        actionPerformed(null);
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

}
