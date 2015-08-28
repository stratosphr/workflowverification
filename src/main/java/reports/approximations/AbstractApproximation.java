package reports.approximations;

import java.util.HashMap;

public abstract class AbstractApproximation {

    public HashMap<String, ?> valuation;

    public AbstractApproximation(HashMap<String, ?> valuation) {
        if (valuation == null) {
            valuation = new HashMap<>();
        }
        this.valuation = valuation;
    }

    public boolean isSAT() {
        return !valuation.isEmpty();
    }

    public abstract boolean isValid();

    public int size() {
        return valuation.size();
    }

}
