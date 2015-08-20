package reports.approximations;

import tools.Prefixes;
import tools.StringTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class MultipleSegmentsApproximation extends AbstractApproximation {

    private ArrayList<TreeMap<String, Integer>> mks;
    private ArrayList<TreeMap<String, Integer>> vpks;
    private ArrayList<TreeMap<String, Integer>> vtks;
    private int nbSegments;

    public MultipleSegmentsApproximation(int nbSegments, HashMap<String, ?> valuation) {
        super(valuation);
        this.nbSegments = nbSegments;
        mks = new ArrayList<>();
        vpks = new ArrayList<>();
        vtks = new ArrayList<>();
        if (isSAT()) {
            int segment = 0;
            mks.add(new TreeMap<String, Integer>());
            for (String varName : valuation.keySet()) {
                if (varName.startsWith(Prefixes.MK + segment + "_")) {
                    mks.get(segment).put(varName.replaceFirst(Prefixes.MK + segment + "_", ""), Integer.parseInt(valuation.get(varName).toString()));
                }
            }
            for (segment = 1; segment <= nbSegments; segment++) {
                mks.add(new TreeMap<String, Integer>());
                vpks.add(new TreeMap<String, Integer>());
                vtks.add(new TreeMap<String, Integer>());
                for (String varName : valuation.keySet()) {
                    if (varName.startsWith(Prefixes.MK + segment + "_")) {
                        mks.get(segment).put(varName.replaceFirst(Prefixes.MK + segment + "_", ""), Integer.parseInt(valuation.get(varName).toString()));
                    }
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
    public String toString() {
        String str = super.toString();
        if (isSAT()) {
            str += "\t\t" + StringTools.separator(20);
            for (int segment = 1; segment <= nbSegments; segment++) {
                String maksSegment = "MAs = [ ";
                String mbksSegment = "MBs = [ ";
                String vpksSegment = "VPs = [ ";
                String vtksSegment = "VTs = [ ";
                for (String varName : mks.get(segment - 1).keySet()) {
                    if (mks.get(segment - 1).get(varName) > 0) {
                        maksSegment += varName + " ";
                    }
                }
                for (String varName : mks.get(segment).keySet()) {
                    if (mks.get(segment).get(varName) > 0) {
                        mbksSegment += varName + " ";
                    }
                }
                for (String varName : vpks.get(segment - 1).keySet()) {
                    if (vpks.get(segment - 1).get(varName) > 0) {
                        vpksSegment += varName + " ";
                    }
                }
                for (String varName : vtks.get(segment - 1).keySet()) {
                    if (vtks.get(segment - 1).get(varName) > 0) {
                        vtksSegment += varName + " ";
                    }
                }
                maksSegment += "]";
                mbksSegment += "]";
                vpksSegment += "]";
                vtksSegment += "]";
                str += "\t\t" + segment + " | " + maksSegment + "\n";
                str += "\t\t" + segment + " | " + mbksSegment + "\n";
                str += "\t\t" + segment + " | " + vpksSegment + "\n";
                str += "\t\t" + segment + " | " + vtksSegment + "\n";
                str += "\t\t" + StringTools.separator(20);
            }
        }
        return str;
    }

}
