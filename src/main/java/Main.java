import files.VerificationFolder;
import mvc2.models.ConfigurationModel;
import mvc2.models.ParametersModel;
import mvc2.controller.Controller;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        setDefaultLookAndFeel("Nimbus");
        /*ConfigurationModel configurationModel = new ConfigurationModel();
        ConfigurationController configurationController = new ConfigurationController(configurationModel, new ParametersModel());
        configurationModel.setVerificationFolder(new VerificationFolder("src/main/resources/examples/big_MG"));
        configurationController.displayMainViews();*/
        Controller controller = new Controller(new ConfigurationModel(), new ParametersModel());
        controller.notifyVerificationFolderChanged(new VerificationFolder("/home/stratosphr/IdeaProjects/workflowverification/src/main/resources/examples/dependants"));
        controller.displayMainViews();
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
