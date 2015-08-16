package mvc.eventsmanagement.events.parameters;

import mvc.model.ParametersModel;

public class CheckOverApproximation2Changed extends VerificationParametersEvent{

    private final boolean newCheckOverApproximation2;

    public CheckOverApproximation2Changed(ParametersModel parametersModel, boolean newCheckOverApproximation2) {
        super(parametersModel);
        this.newCheckOverApproximation2 = newCheckOverApproximation2;
    }

    public boolean getNewCheckOverApproximation2() {
        return newCheckOverApproximation2;
    }


}
