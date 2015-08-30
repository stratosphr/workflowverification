package mvc.views.gui.listeners;

import mvc.controller.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RadioSicstusExplorationHeuristicUpListener extends AbstractListener implements ActionListener {

    public RadioSicstusExplorationHeuristicUpListener(Controller controller) {
        super(controller);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        controller.notifySicstusExplorationHeuristicChanged("up");
    }

}
