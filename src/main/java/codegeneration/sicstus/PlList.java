package codegeneration.sicstus;

import tools.StringTools;

import java.util.ArrayList;
import java.util.Arrays;

public class PlList extends PlTerm {

    private final ArrayList<PlTerm> terms;

    public PlList() {
        this(new ArrayList<PlTerm>());
    }

    public PlList(ArrayList<PlTerm> terms) {
        super("[" + StringTools.join(terms, ", ") + "]");
        this.terms = terms;
    }

    public PlList(PlTerm... terms) {
        this(new ArrayList<PlTerm>(Arrays.asList(terms)));
    }

    public ArrayList<PlTerm> getTerms() {
        return terms;
    }

    public PlTerm getTerm(int index) {
        return terms.get(index);
    }

}
