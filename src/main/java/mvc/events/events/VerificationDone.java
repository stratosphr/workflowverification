package mvc.events.events;

import mvc.models.VerificationModel;
import reports.Report;

public class VerificationDone extends AbstractEvent {

    private final Report report;

    public VerificationDone(VerificationModel source, Report report) {
        super(source);
        this.report = report;
    }

    public Report getReport() {
        return report;
    }

}
