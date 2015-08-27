package verifiers;

import reports.Report;
import reports.approximations.ApproximationTypes;
import specifications.model.SpecificationType;

public interface IVerificationHandler {

    void fireWritingStarted(SpecificationType specificationType, ApproximationTypes approximationType);

    void fireWritingStarted(SpecificationType specificationType, ApproximationTypes approximationType, int segment);

    void fireWritingDone(SpecificationType specificationType, ApproximationTypes approximationType);

    void fireWritingDone(SpecificationType specificationType, ApproximationTypes approximationType, int segment);

    void fireCheckingStarted(SpecificationType specificationType, ApproximationTypes approximationType);

    void fireCheckingStarted(SpecificationType specificationType, ApproximationTypes overApproximation3, int segment);

    void fireCheckingDone(Report report);

}
