package mvc.views;

import mvc.controller.Controller;

public abstract class AbstractView {

    protected final Controller controller;

    public AbstractView(Controller controller) {
        this.controller = controller;
    }

    public abstract void display();

}
