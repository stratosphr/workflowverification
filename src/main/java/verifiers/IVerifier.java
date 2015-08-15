package verifiers;

import reports.MultipleSegmentsApproximation;
import reports.SingleSegmentApproximation;

public interface IVerifier {

    SingleSegmentApproximation checkOverApproximation1();

    SingleSegmentApproximation checkOverApproximation2();

    MultipleSegmentsApproximation checkOverApproximation3();

    MultipleSegmentsApproximation checkUnderApproximation();

}
