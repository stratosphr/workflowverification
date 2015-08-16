package reports.approximations;

import tools.Prefixes;
import tools.StringTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class MultipleSegmentsApproximation extends AbstractApproximation {

    private ArrayList<TreeMap<String, Integer>> vpks;
    private ArrayList<TreeMap<String, Integer>> vtks;
    private int nbSegments;

    public MultipleSegmentsApproximation(int nbSegments, HashMap<String, ?> valuation) {
        super(valuation);
        this.nbSegments = nbSegments;
        vpks = new ArrayList<>();
        vtks = new ArrayList<>();
        if (isSAT()) {
            for (int segment = 1; segment <= nbSegments; segment++) {
                vpks.add(new TreeMap<String, Integer>());
                vtks.add(new TreeMap<String, Integer>());
                for (String varName : valuation.keySet()) {
                    if (varName.startsWith(Prefixes.VPK + segment + "_")) {
                        vpks.get(segment - 1).put(varName.replaceFirst(Prefixes.VPK + segment + "_", ""), Integer.parseInt(valuation.get(varName).toString()));
                    }
                    if (varName.startsWith(Prefixes.VTK + segment + "_")) {
                        vtks.get(segment - 1).put(varName.replaceFirst(Prefixes.VTK + segment + "_", ""), Integer.parseInt(valuation.get(varName).toString()));
                    }
                }
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
        str += "\t\t" + StringTools.separator(20);
        /*for (int segment = 1; segment <= nbSegments; segment++) {
            for (String varName : vpks.get(segment - 1).keySet()) {
                str += segment + " | " + varName + " = " + vpks.get(segment - 1).get(varName) + "\n";
            }
            for (String varName : vtks.get(segment - 1).keySet()) {
                str += segment + " | " + varName + " = " + vtks.get(segment - 1).get(varName) + "\n";
            }
        }*/
        for (int segment = 1; segment <= nbSegments; segment++) {
            String vpksSegment = "VPs = [ ";
            String vtksSegment = "VTs = [ ";
            for (String varName : vpks.get(segment - 1).keySet()) {
                if (vpks.get(segment - 1).get(varName) > 0) {
                    vpksSegment += varName + " ";
                }
            }
            for(String varName : vtks.get(segment - 1).keySet()){
                if (vtks.get(segment - 1).get(varName) > 0) {
                    vtksSegment += varName + " ";
                }
            }
            vpksSegment += "]";
            vtksSegment += "]";
            str += "\t\t" + segment + " | " + vpksSegment + "\n";
            str += "\t\t" + segment + " | " + vtksSegment + "\n";
            str += "\t\t" + StringTools.separator(20);
        }
        return str;
    }

}
