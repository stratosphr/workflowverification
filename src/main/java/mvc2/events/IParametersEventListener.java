package mvc2.events;

import mvc2.events.events.ParametersChanged;

import java.util.EventListener;

public interface IParametersEventListener extends EventListener {

    void parametersChanged(ParametersChanged parametersChanged);

}
