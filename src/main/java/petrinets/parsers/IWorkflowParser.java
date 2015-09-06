package petrinets.parsers;

import petrinets.model.Workflow;

import java.io.File;

public interface IWorkflowParser {

    Workflow parse(File pnmlFile);

}
