package codegeneration.z3;

public class SMTTerm {

    protected Object representation;

    public SMTTerm() {
    }

    public SMTTerm(Object representation) {
        this.representation = representation;
    }

    @Override
    public String toString() {
        return representation.toString();
    }

}
