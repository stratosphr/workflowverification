package codegeneration.sicstus.fd;

import codegeneration.sicstus.PlBooleanExpr;
import codegeneration.sicstus.PlCompoundBooleanExpr;
import codegeneration.sicstus.PlTerm;
import exceptions.NegationCanOnlyHaveOneChildException;

public class PlFDNegation extends PlCompoundBooleanExpr {

    public PlFDNegation() {
        super("#\\ ");
    }

    public PlFDNegation(PlBooleanExpr child) {
        super("#\\ ", child);
    }

    @Override
    public void addParameter(PlTerm child) {
        if (children.size() > 0) {
            throw new NegationCanOnlyHaveOneChildException();
        }else {
            children.add(child);
        }
    }

    @Override
    public String toString() {
        return "(" + separator + children.get(0) + ")";
    }

}
