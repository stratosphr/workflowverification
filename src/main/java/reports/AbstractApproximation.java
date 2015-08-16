package reports;

import java.util.HashMap;

public abstract class AbstractApproximation {

    public HashMap<String, ?> valuation;

    public AbstractApproximation(HashMap<String, ?> valuation) {
        this.valuation = valuation;
    }

    public abstract boolean isSAT();

    @Override
    public String toString() {
        return valuation.toString();
    }

}
