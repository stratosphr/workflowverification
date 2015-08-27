package mvc.models;

import javax.swing.event.EventListenerList;

public class AbstractModel {

    protected EventListenerList eventListeners;

    public AbstractModel() {
        this.eventListeners = new EventListenerList();
    }

}
