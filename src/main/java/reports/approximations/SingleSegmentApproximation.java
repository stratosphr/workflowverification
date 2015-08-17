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
            if (varName.startsWith(Prefixes.MA)) {
                mas.put(varName.replaceFirst(Prefixes.MA, ""), Integer.parseInt(valuation.get(varName).toString()));
            } else if (varName.startsWith(Prefixes.MB)) {
                mbs.put(varName.replaceFirst(Prefixes.MB, ""), Integer.parseInt(valuation.get(varName).toString()));
            } else if (varName.startsWith(Prefixes.VP)) {
                vps.put(varName.replaceFirst(Prefixes.VP, ""), Integer.parseInt(valuation.get(varName).toString()));
            } else if (varName.startsWith(Prefixes.VT)) {
                vts.put(varName.replaceFirst(Prefixes.VT, ""), Integer.parseInt(valuation.get(varName).toString()));
            }
        }
    }

    @Override
    public boolean isSAT() {
        return !valuation.isEmpty();
    }

    @Override
    public String toString() {
        String str = super.toString();
        if (isSAT()) {
            String masSegment = "MAs = [ ";
            String mbsSegment = "MBs = [ ";
            String vpsSegment = "VPs = [ ";
            String vtsSegment = "VTs = [ ";
            for (String varName : mas.keySet()) {
                if (mas.get(varName) > 0) {
                    masSegment += varName + " ";
                }
            }
            for (String varName : mbs.keySet()) {
                if (mbs.get(varName) > 0) {
                    mbsSegment += varName + " ";
                }
            }
            for (String varName : vps.keySet()) {
                if (vps.get(varName) > 0) {
                    vpsSegment += varName + " ";
                }
            }
            for (String varName : vts.keySet()) {
                if (vts.get(varName) > 0) {
                    vtsSegment += varName + " ";
                }
            }
            masSegment += "]";
            mbsSegment += "]";
            vpsSegment += "]";
            vtsSegment += "]";
            str += "\t\t" + masSegment + "\n";
            str += "\t\t" + mbsSegment + "\n";
            str += "\t\t" + vpsSegment + "\n";
            str += "\t\t" + vtsSegment + "\n";
        }
        return str;
    }

}
