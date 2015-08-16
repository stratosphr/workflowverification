package reports.approximations;

import tools.Prefixes;

import java.util.HashMap;
import java.util.TreeMap;

public class SingleSegmentApproximation extends AbstractApproximation {

    private TreeMap<String, Integer> mas;
    private TreeMap<String, Integer> mbs;
    private TreeMap<String, Integer> vps;
    private TreeMap<String, Integer> vts;

    public SingleSegmentApproximation(HashMap<String, ?> valuation) {
        super(valuation);
        mas = new TreeMap<>();
        mbs = new TreeMap<>();
        vps = new TreeMap<>();
        vts = new TreeMap<>();
        for (String varName : valuation.keySet()) {
            if (varName.startsWith(Prefixes.VP)) {
                vps.put(varName.replaceFirst(Prefixes.VP, ""), Integer.parseInt(valuation.get(varName).toString()));
            } else if (varName.startsWith(Prefixes.VT)) {
                vts.put(varName.replaceFirst(Prefixes.VT, ""), Integer.parseInt(valuation.get(varName).toString()));
            } else if (varName.startsWith(Prefixes.MA)) {
                mas.put(varName.replaceFirst(Prefixes.VT, ""), Integer.parseInt(valuation.get(varName).toString()));
            } else if (varName.startsWith(Prefixes.MB)) {
                mbs.put(varName.replaceFirst(Prefixes.VT, ""), Integer.parseInt(valuation.get(varName).toString()));
            }
        }
    }

    @Override
    public boolean isSAT() {
        return false;
    }

    @Override
    public String toString() {
        String str = super.toString();
        for (String varName : mas.keySet()) {
            str += varName + " = " + mas.get(varName) + "\n";
        }
        for (String varName : mbs.keySet()) {
            str += varName + " = " + mbs.get(varName) + "\n";
        }
        for (String varName : vps.keySet()) {
            str += varName + " = " + vps.get(varName) + "\n";
        }
        for (String varName : vts.keySet()) {
            str += varName + " = " + vts.get(varName) + "\n";
        }
        return str;
    }

}
