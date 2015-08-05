package mvc.eventsmanagement;

import mvc.eventsmanagement.events.verificationparametersevents.MaxNodeValuationChanged;
import mvc.eventsmanagement.events.verificationparametersevents.MaxNumberOfSegmentsChanged;
import mvc.eventsmanagement.events.verificationparametersevents.MinNumberOfSegmentsChanged;

public interface IVerificationParametersListener {

    void maxNodeValuationChanged(MaxNodeValuationChanged event);

    void minNumberOfSegmentsChanged(MinNumberOfSegmentsChanged event);

    void maxNumberOfSegmentsChanged(MaxNumberOfSegmentsChanged event);

}
