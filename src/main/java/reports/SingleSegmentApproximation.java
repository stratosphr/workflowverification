package reports;

import java.util.HashMap;

public class SingleSegmentApproximation extends AbstractApproximation {

    public SingleSegmentApproximation(HashMap<String, ?> valuation) {
        super(valuation);
    }

    @Override
    public boolean isSAT() {
        return false;
    }

}
