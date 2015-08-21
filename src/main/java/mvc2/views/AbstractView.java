package mvc2.views;

import mvc2.controller.Controller;

public abstract class AbstractView {

    protected final Controller controller;

    public AbstractView(Controller controller) {
        this.controller = controller;
    }

    public abstract void display();

}
