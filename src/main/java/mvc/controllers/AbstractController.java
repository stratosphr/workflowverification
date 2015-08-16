package mvc.controllers;

import mvc.model.AbstractModel;

public abstract class AbstractController {

    public abstract void displayMainViews();

    abstract void displayParametersViews();

    public abstract void closeMainViews();

    abstract void closeParametersViews();

    public abstract AbstractModel getModel();

}
