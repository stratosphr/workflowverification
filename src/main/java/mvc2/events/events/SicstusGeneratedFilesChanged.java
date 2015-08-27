package mvc2.events.events;

import files.GeneratedCodeFile.SicstusGeneratedCodeFile;
import files.GeneratedReportFile.SicstusGeneratedReportFile;
import mvc2.models.VerificationModel;

public class SicstusGeneratedFilesChanged extends AbstractEvent {

    private final SicstusGeneratedCodeFile sicstusGeneratedCodeFile;
    private final SicstusGeneratedReportFile sicstusGeneratedReportFile;

    public SicstusGeneratedFilesChanged(VerificationModel source, SicstusGeneratedCodeFile sicstusGeneratedCodeFile, SicstusGeneratedReportFile sicstusGeneratedReportFile) {
        super(source);
        this.sicstusGeneratedCodeFile = sicstusGeneratedCodeFile;
        this.sicstusGeneratedReportFile = sicstusGeneratedReportFile;
    }

    public SicstusGeneratedCodeFile getSicstusGeneratedCodeFile() {
        return sicstusGeneratedCodeFile;
    }

    public SicstusGeneratedReportFile getSicstusGeneratedReportFile() {
        return sicstusGeneratedReportFile;
    }

}
