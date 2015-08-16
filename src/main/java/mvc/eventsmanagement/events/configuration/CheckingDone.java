package mvc.eventsmanagement.events.configuration;

import mvc.model.ConfigurationModel;
import reports.AbstractApproximation;
import verifiers.AbstractVerifier;

public class CheckingDone extends Z3VerificationDone {

    private final AbstractVerifier usedVerifier;
    private final AbstractApproximation approximation;

    public CheckingDone(ConfigurationModel configurationModel, AbstractVerifier usedVerifier, AbstractApproximation approximation) {
        super(configurationModel);
        this.usedVerifier = usedVerifier;
        this.approximation = approximation;
    }

    public AbstractVerifier getUsedVerifier() {
        return usedVerifier;
    }

    public AbstractApproximation getApproximation() {
        return approximation;
    }

}
