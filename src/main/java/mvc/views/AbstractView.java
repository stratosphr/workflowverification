package mvc.views;

import mvc.controllers.AbstractController;
import mvc.model.AbstractModel;

public abstract class AbstractView {

    private final AbstractController controller;
    private final AbstractModel model;

    public AbstractView(AbstractController controller, AbstractModel model) {
        this.controller = controller;
        this.model = model;
        buildView();
    }

    public abstract void buildView();

    public AbstractController getController() {
        return controller;
    }

    public AbstractModel getModel() {
        return model;
    }

    public abstract void display();

    public abstract void close();

}
