package mvc.events;

import mvc.events.events.ParametersChanged;

import java.util.EventListener;

public interface IParametersEventListener extends EventListener {

    void parametersChanged(ParametersChanged parametersChanged);

}
