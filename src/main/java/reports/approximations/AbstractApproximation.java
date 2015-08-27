package reports.approximations;

import java.util.HashMap;

public abstract class AbstractApproximation {

    public HashMap<String, ?> valuation;

    public AbstractApproximation(HashMap<String, ?> valuation) {
        this.valuation = valuation;
    }

    public boolean isSAT() {
        return !valuation.isEmpty();
    }

    public int size() {
        return valuation.size();
    }

}
