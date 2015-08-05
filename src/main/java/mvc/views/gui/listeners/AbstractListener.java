package mvc.views.gui.listeners;

import mvc.controllers.AbstractController;

public class AbstractListener {

    private final AbstractController controller;

    public AbstractListener(AbstractController controller) {
        this.controller = controller;
    }

    public AbstractController getController() {
        return controller;
    }

}
