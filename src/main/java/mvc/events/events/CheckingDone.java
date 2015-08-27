package mvc.events.events;

import mvc.models.VerificationModel;
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
