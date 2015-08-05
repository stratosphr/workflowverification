package mvc.views.console;

import mvc.controllers.ParametersController;
import mvc.eventsmanagement.events.verificationparametersevents.MaxNodeValuationChanged;
import mvc.eventsmanagement.events.verificationparametersevents.MaxNumberOfSegmentsChanged;
import mvc.eventsmanagement.events.verificationparametersevents.MinNumberOfSegmentsChanged;
import mvc.model.ParametersModel;
import mvc.views.AbstractParametersView;

public class ConsoleParametersView extends AbstractParametersView {

    private String representation;

    public ConsoleParametersView(ParametersController parametersController, ParametersModel parametersModel) {
        super(parametersController, parametersModel);
    }

    @Override
    public void buildView() {
        representation = "##################\n";
        representation += "### Parameters ###\n";
        representation += "##################\n";
    }

    @Override
    public void display() {
        buildView();
        System.out.println(representation);
    }

    @Override
    public void close() {
        System.out.println("PARAMETERS VIEW CLOSED");
    }

    @Override
    public void maxNodeValuationChanged(MaxNodeValuationChanged event) {

    }

    @Override
    public void minNumberOfSegmentsChanged(MinNumberOfSegmentsChanged event) {

    }

    @Override
    public void maxNumberOfSegmentsChanged(MaxNumberOfSegmentsChanged event) {

    }

}
