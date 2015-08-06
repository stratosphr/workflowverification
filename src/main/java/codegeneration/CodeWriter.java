package codegeneration;

import files.GeneratedCodeFile.GeneratedCodeFile;
import mvc.model.ConfigurationModel;

public class CodeWriter extends SimpleWriter {

    private final ConfigurationModel configurationModel;

    public CodeWriter(GeneratedCodeFile generatedCodeFile, ConfigurationModel configurationModel) {
        super(generatedCodeFile);
        this.configurationModel = configurationModel;
    }

    public void writeStateEquation() {
        write(configurationModel.getImplementation().getStateEquation());
    }

}
