package verifiers;

import reports.approximations.MultipleSegmentsApproximation;
import reports.approximations.SingleSegmentApproximation;

public interface IVerifier {

    SingleSegmentApproximation checkOverApproximation1();

    SingleSegmentApproximation checkOverApproximation2();

    MultipleSegmentsApproximation checkOverApproximation3(int nbSegments);

    MultipleSegmentsApproximation checkUnderApproximation(int nbSegments);

}
