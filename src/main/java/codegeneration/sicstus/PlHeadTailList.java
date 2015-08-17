package codegeneration.sicstus;

import tools.StringTools;

import java.util.ArrayList;
import java.util.Arrays;

public class PlHeadTailList extends PlTerm {

    private final ArrayList<PlTerm> headTerms;
    private final PlTerm tail;

    public PlHeadTailList(PlTerm head, PlTerm tail) {
        this(new ArrayList<>(Arrays.asList(new PlTerm[]{head})), tail);
    }

    public PlHeadTailList(PlTerm[] head, PlTerm tail) {
        this(new ArrayList<>(Arrays.asList(head)), tail);
    }

    public PlHeadTailList(ArrayList<PlTerm> headTerms, PlTerm tail) {
        super("[" + StringTools.join(headTerms, ", ") + "|" + tail + "]");
        this.headTerms = headTerms;
        this.tail = tail;
    }

}
