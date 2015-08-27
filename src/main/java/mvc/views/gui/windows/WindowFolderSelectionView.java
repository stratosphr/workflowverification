package mvc.views.gui.windows;

import mvc.controller.Controller;
import mvc.events.events.VerificationFolderChanged;
import mvc.views.AbstractFolderSelectionView;
import mvc.views.gui.listeners.FileChooserListener;

import javax.swing.*;

public class WindowFolderSelectionView extends AbstractFolderSelectionView {

    private JFileChooser fileChooser;

    public WindowFolderSelectionView(Controller controller) {
        super(controller);
        buildFrame();
        addEventListeners();
    }

    private void addEventListeners() {
        fileChooser.addActionListener(new FileChooserListener(controller));
    }

    private void buildFrame() {
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    @Override
    public void display() {
        fileChooser.showOpenDialog(null);
    }

    @Override
    public void verificationFolderChanged(VerificationFolderChanged verificationFolderChanged) {
        fileChooser.setCurrentDirectory(verificationFolderChanged.getVerificationFolder().getParentFile());
    }

}
