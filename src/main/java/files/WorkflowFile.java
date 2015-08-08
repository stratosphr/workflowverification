package files;

import petrinets.PIPEParser;
import petrinets.model.Workflow;

import java.io.File;

public class WorkflowFile extends File {

    public WorkflowFile(String path) {
        super(path);
    }

    public Workflow extractWorkflow() {
        return PIPEParser.parse(this);
    }

    public boolean isValid() {
        return isFile() && getName().equals(getParentFile().getName() + ".xml") && getAbsolutePath().endsWith(".xml");
    }

}
