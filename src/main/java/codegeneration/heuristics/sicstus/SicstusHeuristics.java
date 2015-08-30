package codegeneration.heuristics.sicstus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SicstusHeuristics {

    public static List<String> getSelectionHeuristics() {
        return new ArrayList<>(Arrays.asList(new String[]{
                "leftmost",
                "min",
                "max",
                "first_fail",
                "anti_first_fail",
                "occurrence",
                "ffc",
                "max_regret"
        }));
    }

    public static List<String> getChoicesHeuristics() {
        return new ArrayList<>(Arrays.asList(new String[]{
                "step",
                "enum",
                "bisect",
                "median",
                "middle",
        }));
    }

}
