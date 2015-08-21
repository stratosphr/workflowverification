package mvc2.events.events;

import files.GeneratedCodeFile.Z3GeneratedCodeFile;
import files.GeneratedReportFile.Z3GeneratedReportFile;
import mvc2.models.ConfigurationModel;

public class Z3GeneratedFilesChanged extends AbstractEvent {

    private final Z3GeneratedCodeFile z3GeneratedCodeFile;
    private final Z3GeneratedReportFile z3GeneratedReportFile;

    public Z3GeneratedFilesChanged(ConfigurationModel source, Z3GeneratedCodeFile z3GeneratedCodeFile, Z3GeneratedReportFile z3GeneratedReportFile) {
        super(source);
        this.z3GeneratedCodeFile = z3GeneratedCodeFile;
        this.z3GeneratedReportFile = z3GeneratedReportFile;
    }

    public Z3GeneratedCodeFile getZ3GeneratedCodeFile() {
        return z3GeneratedCodeFile;
    }

    public Z3GeneratedReportFile getZ3GeneratedReportFile() {
        return z3GeneratedReportFile;
    }

}
