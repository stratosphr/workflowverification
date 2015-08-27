package mvc.views;

import mvc.controller.Controller;
import mvc.events.IVerificationEventListener;
import mvc.events.IVerificationFolderChangedListener;

public abstract class AbstractVerificationView extends AbstractView implements IVerificationEventListener, IVerificationFolderChangedListener {

    public AbstractVerificationView(Controller controller) {
        super(controller);
    }

}
