package codegeneration.sicstus;

import exceptions.UnparsablePlTermException;
import se.sics.jasper.Term;

import java.util.ArrayList;

public class PlTerm {

    private Object representation;

    public PlTerm() {
        this("");
    }

    public PlTerm(Object representation) {
        this.representation = representation;
    }

    public static PlTerm parsePlTerm(Term term) {
        try {
            if (term.isInteger()) {
                return new PlTerm(Integer.parseInt(term.toString()));
            } else if (term.isFloat()) {
                return new PlTerm(Float.parseFloat(term.toString()));
            } else if (term.isEmptyList()) {
                return new PlList();
            } else if (term.isList()) {
                ArrayList<PlTerm> terms = new ArrayList<PlTerm>();
                for (Term t : term.toPrologTermArray()) {
                    terms.add(PlTerm.parsePlTerm(t));
                }
                return new PlList(terms);
            } else if (term.isAtomic()) {
                return new PlTerm(term);
            } else if (term.isAtom()) {
                return new PlTerm(term);
            } else if (term.isVariable()) {
                return new PlTerm(term);
            } else {
                throw new UnparsablePlTermException(term);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean isInteger() {
        return getRepresentation() instanceof Integer;
    }

    public boolean isFloat() {
        return getRepresentation() instanceof Float;
    }

    public boolean isList() {
        return this instanceof PlList;
    }

    public Object getRepresentation() {
        return representation;
    }

    @Override
    public String toString() {
        return representation.toString();
    }

}
