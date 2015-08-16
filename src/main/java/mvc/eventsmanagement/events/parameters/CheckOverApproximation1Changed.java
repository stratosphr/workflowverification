package mvc.eventsmanagement.events.parameters;

import mvc.model.ParametersModel;

public class CheckOverApproximation1Changed extends VerificationParametersEvent{

    private final boolean newCheckOverApproximation1;

    public CheckOverApproximation1Changed(ParametersModel parametersModel, boolean newCheckOverApproximation1) {
        super(parametersModel);
        this.newCheckOverApproximation1 = newCheckOverApproximation1;
    }

    public boolean getNewCheckOverApproximation1() {
        return newCheckOverApproximation1;
    }


}
