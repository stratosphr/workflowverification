package verifiers;

import reports.OverApproximation;

public interface IVerifier {

    OverApproximation checkOverApproximation1();

    OverApproximation checkOverApproximation2();

    OverApproximation checkOverApproximation3();

    OverApproximation checkUnderApproximation();

}
