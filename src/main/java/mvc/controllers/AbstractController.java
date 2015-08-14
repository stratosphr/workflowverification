package mvc.controllers;

import mvc.model.AbstractModel;

public abstract class AbstractController {

    public abstract void displayViews();

    public abstract void closeViews();

    public abstract AbstractModel getModel();

}
