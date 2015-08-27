package mvc.views;

import mvc.controller.Controller;
import mvc.events.IParametersEventListener;

public abstract class AbstractParametersView extends AbstractView implements IParametersEventListener {

    public AbstractParametersView(Controller controller) {
        super(controller);
    }

    public abstract void close();

}
