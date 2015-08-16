package mvc.eventsmanagement.events.configuration;

import mvc.model.ConfigurationModel;
import reports.Report;

public class CheckingDone extends AbstractConfigurationEvent{

    private final Report report;

    public CheckingDone(ConfigurationModel configurationModel, Report report) {
        super(configurationModel);
        this.report = report;
    }

    public Report getReport() {
        return report;
    }

}
