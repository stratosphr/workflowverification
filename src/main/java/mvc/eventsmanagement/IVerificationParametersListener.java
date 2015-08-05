package mvc.eventsmanagement;

import mvc.eventsmanagement.events.parameters.MaxNodeValuationChanged;
import mvc.eventsmanagement.events.parameters.MaxNumberOfSegmentsChanged;
import mvc.eventsmanagement.events.parameters.MinNumberOfSegmentsChanged;

public interface IVerificationParametersListener {

    void maxNodeValuationChanged(MaxNodeValuationChanged event);

    void minNumberOfSegmentsChanged(MinNumberOfSegmentsChanged event);

    void maxNumberOfSegmentsChanged(MaxNumberOfSegmentsChanged event);

}
