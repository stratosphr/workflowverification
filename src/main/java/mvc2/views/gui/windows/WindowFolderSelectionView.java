package mvc2.views.gui.windows;

import mvc2.controller.Controller;
import mvc2.views.AbstractFolderSelectionView;
import mvc2.views.gui.listeners.FileChooserListener;

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

}
