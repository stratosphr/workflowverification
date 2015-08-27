package mvc2.views;

import mvc2.controller.Controller;
import mvc2.events.IVerificationEventListener;
import mvc2.events.IVerificationFolderChangedListener;

public abstract class AbstractVerificationView extends AbstractView implements IVerificationEventListener, IVerificationFolderChangedListener {

    public AbstractVerificationView(Controller controller) {
        super(controller);
    }

}
