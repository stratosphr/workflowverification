package verifiers;

import reports.MultipleSegmentsApproximation;
import reports.SingleSegmentApproximation;

public interface IVerificationHandler {

    void doneCheckingOverApproximation1(SingleSegmentApproximation approximation);

    void doneCheckingOverApproximation2(SingleSegmentApproximation approximation);

    void doneCheckingOverApproximation3(MultipleSegmentsApproximation approximation);

    void doneCheckingUnderApproximation(MultipleSegmentsApproximation approximation);

}
