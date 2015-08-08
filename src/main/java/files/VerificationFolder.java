package files;

import java.io.File;

public class VerificationFolder extends File {

    private SpecificationFolder specificationFolder;
    private WorkflowFile workflowFile;

    public VerificationFolder(String path) {
        super(path);
        this.specificationFolder = getSpecificationFolder();
        this.workflowFile = getWorkflowFile();
    }

    public boolean isValid() {
        return isDirectory() && workflowFile != null && specificationFolder != null;
    }

    public WorkflowFile getWorkflowFile() {
        if (workflowFile != null) {
            return workflowFile;
        } else {
            File[] files = listFiles();
            if (files != null) {
                for (File file : files) {
                    WorkflowFile workflowFile = new WorkflowFile(file.getAbsolutePath());
                    if (workflowFile.isValid()) {
                        this.workflowFile = workflowFile;
                        break;
                    }
                }
            }
            return workflowFile;
        }
    }

    public SpecificationFolder getSpecificationFolder() {
        if (specificationFolder != null) {
            return specificationFolder;
        } else {
            File[] files = listFiles();
            if (files == null || files.length == 0) {
                return null;
            } else {
                for (File file : files) {
                    SpecificationFolder specificationFolder = new SpecificationFolder(file.getAbsolutePath());
                    if (specificationFolder.isValid()) {
                        this.specificationFolder = specificationFolder;
                    }
                }
                return specificationFolder;
            }
        }
    }

}
