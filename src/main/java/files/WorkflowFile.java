package files;

import petrinets.model.Workflow;
import petrinets.parsers.HadaraParser;

import java.io.File;

public class WorkflowFile extends File {

    public WorkflowFile(String path) {
        super(path);
    }

    public Workflow extractWorkflow() {
        //return new PIPEParser().parse(this);
        return new HadaraParser().parse(this);
    }

    public boolean isValid() {
        return isFile() && getName().equals(getParentFile().getName() + ".xml") && getAbsolutePath().endsWith(".xml");
    }

}
