package mvc2.events.events;

import mvc2.models.ConfigurationModel;
import reports.Report;

public class DoneChecking extends AbstractEvent {

    private final Report report;

    public DoneChecking(ConfigurationModel source, Report report) {
        super(source);
        this.report = report;
    }

    public Report getReport() {
        return report;
    }

}
