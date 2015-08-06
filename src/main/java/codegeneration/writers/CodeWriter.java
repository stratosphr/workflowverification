package codegeneration.writers;

import mvc.model.ConfigurationModel;

public abstract class CodeWriter {

    protected final ConfigurationModel configurationModel;

    public CodeWriter(ConfigurationModel configurationModel) {
        this.configurationModel = configurationModel;
    }

    public abstract void writeStateEquation();

    public void write(String stateEquation) {
        System.out.println("CodeWriter.write :\n" + stateEquation);
    }

}
