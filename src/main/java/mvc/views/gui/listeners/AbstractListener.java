package mvc.views.gui.listeners;

import mvc.controller.Controller;

public class AbstractListener {

    protected Controller controller;

    public AbstractListener(Controller controller) {
        this.controller = controller;
    }

}
