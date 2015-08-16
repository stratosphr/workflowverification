package mvc.eventsmanagement.events.parameters;

import mvc.model.ParametersModel;

public class CheckUnderApproximationChanged extends VerificationParametersEvent{

    private final boolean newCheckUnderApproximation;

    public CheckUnderApproximationChanged(ParametersModel parametersModel, boolean newCheckUnderApproximation) {
        super(parametersModel);
        this.newCheckUnderApproximation = newCheckUnderApproximation;
    }

    public boolean getNewCheckUnderApproximation() {
        return newCheckUnderApproximation;
    }


}
