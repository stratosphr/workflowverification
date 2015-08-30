package codegeneration.sicstus;

import tools.StringTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlList extends PlTerm {

    private final List<PlTerm> terms;

    public PlList() {
        this(new ArrayList<PlTerm>());
    }

    public PlList(List<PlTerm> terms) {
        super("[" + StringTools.join(terms, ", ") + "]");
        this.terms = terms;
    }

    public PlList(PlTerm... terms){
        this(new ArrayList<>(Arrays.asList(terms)));
    }

    public List<PlTerm> getTerms() {
        return terms;
    }

    public PlTerm getTerm(int index) {
        return terms.get(index);
    }

}
