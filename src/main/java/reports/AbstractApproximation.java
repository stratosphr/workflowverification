package reports;

import java.util.HashMap;

public abstract class AbstractApproximation {

    public HashMap<String, ?> valuation;

    public AbstractApproximation(HashMap<String, ?> valuation) {
        this.valuation = valuation;
    }

    public abstract boolean isSAT();

    public int size() {
        return valuation.size();
    }

    @Override
    public String toString() {
        String str = "Approximation : \n";
        str += "\tSAT : " + isSAT() + "\n";
        if (isSAT()) {
            str += "\tSegments : \n";
            str += valuation;
        }
        return str;
    }

}
