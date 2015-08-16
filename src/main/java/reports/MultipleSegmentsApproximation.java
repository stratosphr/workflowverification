package reports;

import java.util.HashMap;

public class MultipleSegmentsApproximation extends AbstractApproximation {

    public MultipleSegmentsApproximation(HashMap<String, ?> valuation) {
        super(valuation);
    }

    @Override
    public boolean isSAT() {
        return !valuation.isEmpty();
    }

}
