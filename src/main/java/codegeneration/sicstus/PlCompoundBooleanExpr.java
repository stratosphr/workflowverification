package codegeneration.sicstus;

import tools.StringTools;

import java.util.ArrayList;
import java.util.Arrays;

public class PlCompoundBooleanExpr extends PlBooleanExpr {

    protected String separator;
    protected ArrayList<PlTerm> children;

    public PlCompoundBooleanExpr(String separator) {
        this(separator, new ArrayList<PlTerm>());
    }

    public PlCompoundBooleanExpr(PlTerm... children) {
        this(" ", children);
    }

    public PlCompoundBooleanExpr(String separator, PlTerm... children) {
        this(separator, new ArrayList<>(Arrays.asList(children)));
    }

    public PlCompoundBooleanExpr(String separator, ArrayList<PlTerm> children) {
        this.separator = separator;
        this.children = children;
    }

    public void addParameter(PlTerm child) {
        children.add(child);
    }

    @Override
    public String toString() {
        return "(" + StringTools.join(children, separator) + ")";
    }

}
