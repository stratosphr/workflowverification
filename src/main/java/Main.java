import files.VerificationFolder;
import mvc.controller.Controller;
import mvc.models.ParametersModel;
import mvc.models.VerificationModel;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        setDefaultLookAndFeel("Nimbus");
        Controller controller = new Controller(new VerificationModel(), new ParametersModel());
        controller.notifyVerificationFolderChanged(new VerificationFolder("/home/stratosphr/IdeaProjects/workflowverification/src/main/resources/examples/segments_6"));
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
