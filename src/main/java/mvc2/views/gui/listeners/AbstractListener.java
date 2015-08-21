package mvc2.views.gui.listeners;

import mvc2.controller.Controller;

public class AbstractListener {

    protected Controller controller;

    public AbstractListener(Controller controller) {
        this.controller = controller;
    }

}
