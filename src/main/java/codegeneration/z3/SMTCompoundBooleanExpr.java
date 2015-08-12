package codegeneration.z3;

import codegeneration.z3.SMTBooleanExpr;
import codegeneration.z3.SMTTerm;

import java.util.ArrayList;

public class SMTCompoundBooleanExpr extends SMTBooleanExpr {

    private ArrayList<SMTTerm> children;

    public SMTCompoundBooleanExpr(String name, ArrayList<SMTTerm> children) {
        super(name);
        this.children = children;
    }

}
