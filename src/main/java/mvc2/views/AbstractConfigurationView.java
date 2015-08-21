package mvc2.views;

import mvc2.controller.Controller;
import mvc2.events.IConfigurationEventListener;

public abstract class AbstractConfigurationView extends AbstractView implements IConfigurationEventListener {

    public AbstractConfigurationView(Controller controller) {
        super(controller);
    }

}
