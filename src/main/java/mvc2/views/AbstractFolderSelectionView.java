package mvc2.views;

import mvc2.controller.Controller;
import mvc2.events.IVerificationFolderChangedListener;

public abstract class AbstractFolderSelectionView extends AbstractView implements IVerificationFolderChangedListener {

    public AbstractFolderSelectionView(Controller controller) {
        super(controller);
    }

}
