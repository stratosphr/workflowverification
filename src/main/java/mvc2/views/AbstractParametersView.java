package mvc2.views;

import mvc2.controller.Controller;
import mvc2.events.IParametersEventListener;

public abstract class AbstractParametersView extends AbstractView implements IParametersEventListener {

    public AbstractParametersView(Controller controller) {
        super(controller);
    }

    public abstract void close();

}
