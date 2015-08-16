package mvc.eventsmanagement;

import mvc.eventsmanagement.events.parameters.*;

import java.util.EventListener;

public interface IParametersListener extends EventListener {

    void maxNodeValuationChanged(MaxNodeValuationChanged event);

    void minNumberOfSegmentsChanged(MinNumberOfSegmentsChanged event);

    void maxNumberOfSegmentsChanged(MaxNumberOfSegmentsChanged event);

    void checkOverApproximation1Changed(CheckOverApproximation1Changed event);

    void checkOverApproximation2Changed(CheckOverApproximation2Changed event);

    void checkOverApproximation3Changed(CheckOverApproximation3Changed event);

    void checkUnderApproximationChanged(CheckUnderApproximationChanged event);

}
