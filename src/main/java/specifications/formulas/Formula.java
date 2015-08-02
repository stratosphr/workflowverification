package specifications.formulas;

public abstract class Formula {

    protected String name;

    public Formula(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
