package codegeneration.z3;

public enum ESMTType {

    BOOL, INT;

    @Override
    public String toString() {
        String type = "";
        type += super.toString().substring(0, 1) + super.toString().substring(1).toLowerCase();
        return type;
    }

}
