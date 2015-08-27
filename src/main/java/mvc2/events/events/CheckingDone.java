package mvc2.events.events;

import mvc2.models.VerificationModel;
import reports.Report;

public class CheckingDone extends AbstractEvent {

    private final Report report;

    public CheckingDone(VerificationModel source, Report report) {
        super(source);
        this.report = report;
    }

    public Report getReport() {
        return report;
    }

}
