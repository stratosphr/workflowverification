package gui.listeners;

import gui.VerificationParametersDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BtnParametersListener implements ActionListener {

    public void actionPerformed(ActionEvent actionEvent) {
        VerificationParametersDialog.main(null);
    }

}
