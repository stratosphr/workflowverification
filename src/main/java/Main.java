import files.SpecificationFolder;
import files.VerificationFolder;
import mvc.controllers.ConfigurationController;
import mvc.model.ConfigurationModel;
import mvc.model.ParametersModel;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        setDefaultLookAndFeel("Nimbus");
        ConfigurationModel configurationModel = new ConfigurationModel();
        ConfigurationController configurationController = new ConfigurationController(configurationModel, new ParametersModel());
        configurationModel.setVerificationFolder(new VerificationFolder("src/main/resources/examples/big_siphonMG"));
        SpecificationFolder specificationFolder = configurationModel.getVerificationFolder().getSpecificationFolder();
        configurationController.displayViews();
    }

    private static void setDefaultLookAndFeel(String lookAndFeelName) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (lookAndFeelName.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
