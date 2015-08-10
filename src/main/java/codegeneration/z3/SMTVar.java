package codegeneration.z3;

public class SMTVar extends SMTTerm {

    private final ESMTType type;

    public SMTVar(String name, ESMTType type) {
        super(name);
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }

    public String toTypedVar() {
        return "(" + name + " " + type + ")";
    }

}
