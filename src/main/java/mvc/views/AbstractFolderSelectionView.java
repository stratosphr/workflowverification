package mvc.views;

import mvc.controller.Controller;
import mvc.events.IVerificationFolderChangedListener;

public abstract class AbstractFolderSelectionView extends AbstractView implements IVerificationFolderChangedListener {

    public AbstractFolderSelectionView(Controller controller) {
        super(controller);
    }

}
