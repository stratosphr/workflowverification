package specifications;

import specifications.formulas.Formula;

public class Specification {

    private String name;
    private SpecificationType type;
    private Formula formula;

    public Specification(String name, SpecificationType type, Formula formula) {
        this.name = name;
        this.type = type;
        this.formula = formula;
    }

    public String getName() {
        return name;
    }

    public SpecificationType getType() {
        return type;
    }

    public Formula getFormula() {
        return formula;
    }

    @Override
    public String toString() {
        String str = "Specification : \n";
        str += "\t   name : " + name + "\n";
        str += "\t   type : " + type + "\n";
        str += "\tformula : " + formula + "\n";
        return str;
    }

}
