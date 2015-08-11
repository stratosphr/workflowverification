package codegeneration.z3;

import com.sun.istack.internal.NotNull;
import exceptions.NegationCanOnlyHaveOneChildException;

import java.util.ArrayList;

public class SMTNegation extends SMTPredicateCall {

    public SMTNegation(){
        this(new ArrayList<SMTTerm>());
    }

    public SMTNegation(@NotNull ArrayList<SMTTerm> parameters) {
        super("not", parameters);
    }

    @Override
    public void addParameter(SMTTerm parameter) {
        if (parameters.size() > 0) {
            throw new NegationCanOnlyHaveOneChildException();
        }else {
            super.addParameter(parameter);
        }
    }

}
