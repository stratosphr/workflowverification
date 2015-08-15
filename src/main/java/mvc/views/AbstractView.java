package mvc.views;

import mvc.controllers.ConfigurationController;
import mvc.model.AbstractModel;

public abstract class AbstractView {

    private final ConfigurationController controller;
    private final AbstractModel model;

    public AbstractView(ConfigurationController controller, AbstractModel model) {
        this.controller = controller;
        this.model = model;
        buildView();
    }

    public abstract void buildView();

    public ConfigurationController getController() {
        return controller;
    }

    public AbstractModel getModel() {
        return model;
    }

    public abstract void display();

    public abstract void close();

}
