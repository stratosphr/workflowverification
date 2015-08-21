package verifiers;

import reports.approximations.AbstractApproximation;
import reports.approximations.ApproximationTypes;

public interface IVerificationHandler {

    void fireDoneChecking(ApproximationTypes approximationType, AbstractApproximation approximation);

}
