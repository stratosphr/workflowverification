package codegeneration.sicstus.fd;

import codegeneration.sicstus.PlBooleanExpr;
import codegeneration.sicstus.PlCompoundBooleanExpr;
import codegeneration.sicstus.PlTerm;
import com.sun.istack.internal.NotNull;
import exceptions.NegationCanOnlyHaveOneChildException;

public class PlFDNegation extends PlCompoundBooleanExpr {

    public PlFDNegation() {
        super("#\\ ");
    }

    public PlFDNegation(@NotNull PlBooleanExpr child) {
        super("#\\ ", child);
    }

    @Override
    public void addChild(PlTerm child) {
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
