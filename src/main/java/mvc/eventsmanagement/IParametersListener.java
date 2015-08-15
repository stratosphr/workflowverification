package mvc.eventsmanagement;

import mvc.eventsmanagement.events.parameters.MaxNodeValuationChanged;
import mvc.eventsmanagement.events.parameters.MaxNumberOfSegmentsChanged;
import mvc.eventsmanagement.events.parameters.MinNumberOfSegmentsChanged;

import java.util.EventListener;

public interface IParametersListener extends EventListener {

    void maxNodeValuationChanged(MaxNodeValuationChanged event);

    void minNumberOfSegmentsChanged(MinNumberOfSegmentsChanged event);

    void maxNumberOfSegmentsChanged(MaxNumberOfSegmentsChanged event);

}
