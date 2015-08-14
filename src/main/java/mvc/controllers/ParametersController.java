package mvc.controllers;

import mvc.model.ConfigurationModel;
import mvc.model.ParametersModel;
import mvc.views.console.ConsoleParametersView;
import mvc.views.gui.WindowParametersView;

public class ParametersController extends AbstractController {

    private ParametersModel parametersModel;
    private ConsoleParametersView consoleView;
    private WindowParametersView windowView;

    public ParametersController(ParametersModel parametersModel) {
        this.parametersModel = parametersModel;
        consoleView = new ConsoleParametersView(this, parametersModel);
        windowView = new WindowParametersView(this, parametersModel);
    }

    @Override
    public void displayViews() {
        consoleView.display();
        windowView.display();
    }

    @Override
    public void closeViews() {
        consoleView.close();
        windowView.close();
    }

    @Override
    public ParametersModel getModel() {
        return parametersModel;
    }

    public void notifyMaxNodeValuationChanged(int newMaxNodeValuation) {
        parametersModel.setMaxNodeValuation(newMaxNodeValuation);
    }

    public void notifyMinNumberOfSegmentsChanged(int minNumberOfSegments) {
        parametersModel.setMinNumberOfSegments(minNumberOfSegments);
    }

    public void notifyMaxNumberOfSegmentsChanged(int maxNumberOfSegments) {
        parametersModel.setMaxNumberOfSegments(maxNumberOfSegments);
    }

    public void notifyParametersEditionValidated() {
        closeViews();
    }

    public void notifyParametersEditionAborted() {
        closeViews();
    }

}
