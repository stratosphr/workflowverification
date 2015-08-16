package mvc.eventsmanagement.events.parameters;

import mvc.model.ParametersModel;

public class CheckOverApproximation3Changed extends VerificationParametersEvent{

    private final boolean newCheckOverApproximation3;

    public CheckOverApproximation3Changed(ParametersModel parametersModel, boolean newCheckOverApproximation3) {
        super(parametersModel);
        this.newCheckOverApproximation3 = newCheckOverApproximation3;
    }

    public boolean getNewCheckOverApproximation3() {
        return newCheckOverApproximation3;
    }


}
