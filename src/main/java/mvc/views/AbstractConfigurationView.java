package mvc.views;

import mvc.controllers.ConfigurationController;
import mvc.eventsmanagement.IVerificationConfigurationListener;
import mvc.model.ConfigurationModel;

public abstract class AbstractConfigurationView extends AbstractView implements IVerificationConfigurationListener {

    public AbstractConfigurationView(ConfigurationController configurationController, ConfigurationModel configurationModel) {
        super(configurationController, configurationModel);
    }

    public ConfigurationController getConfigurationController() {
        return (ConfigurationController) getController();
    }

}
