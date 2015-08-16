package mvc.views.gui.listeners.parameters;

import mvc.controllers.ConfigurationController;
import mvc.views.gui.WindowParametersView;
import mvc.views.gui.listeners.AbstractListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BtnOkListener extends AbstractListener implements ActionListener {

    private WindowParametersView windowParametersView;

    public BtnOkListener(WindowParametersView windowParametersView) {
        super(windowParametersView.getController());
        this.windowParametersView = windowParametersView;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        ((ConfigurationController) getController()).notifyMaxNodeValuationChanged(windowParametersView.getSpecifiedMaxNodeValuation());
        ((ConfigurationController) getController()).notifyMinNumberOfSegmentsChanged(windowParametersView.getSpecifiedMinNumberOfSegments());
        ((ConfigurationController) getController()).notifyMaxNumberOfSegmentsChanged(windowParametersView.getSpecifiedMaxNumberOfSegments());
        ((ConfigurationController) getController()).notifyCheckOverApproximation1Changed(windowParametersView.getCheckOverApproximation1isSelected());
        ((ConfigurationController) getController()).notifyCheckOverApproximation2Changed(windowParametersView.getCheckOverApproximation2isSelected());
        ((ConfigurationController) getController()).notifyCheckOverApproximation3Changed(windowParametersView.getCheckOverApproximation3isSelected());
        ((ConfigurationController) getController()).notifyCheckUnderApproximationChanged(windowParametersView.getCheckUnderApproximationisSelected());
        ((ConfigurationController) getController()).notifyParametersEditionValidated();
    }

}
