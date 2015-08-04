package mvc.views.gui;

import codegeneration.implementations.sicstus.ESicstusImplementation;
import codegeneration.implementations.z3.EZ3Implementation;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import files.GeneratedCodeFile.SicstusGeneratedCodeFile;
import files.GeneratedCodeFile.Z3GeneratedCodeFile;
import files.GeneratedReportFile.SicstusGeneratedReportFile;
import files.GeneratedReportFile.Z3GeneratedReportFile;
import files.SpecificationFile;
import files.VerificationFolder;
import mvc.controllers.VerificationController;
import mvc.eventsmanagement.VerificationParametersChanged;
import mvc.model.VerificationParameters;
import mvc.views.VerificationView;
import mvc.views.gui.items.SpecificationItem;
import mvc.views.gui.listeners.BtnParametersListener;
import mvc.views.gui.listeners.CbxSpecificationFileListener;
import specifications.model.Specification;
import specifications.model.SpecificationType;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class MainWindow extends VerificationView {

    private JTabbedPane tab_main;

    private JPanel panel_verification;
    private JTextField txt_verificationFolder;
    private JButton btn_verificationFolder;
    private JComboBox<SpecificationItem> cbx_specificationFile;
    private JRadioButton radio_may;
    private JRadioButton radio_must;
    private JButton btn_parameters;
    private JButton btn_sicstusVerifySpecification;
    private JComboBox<ESicstusImplementation> cbx_sicstusImplementations;
    private JButton btn_z3VerifySpecification;
    private JComboBox<EZ3Implementation> cbx_z3Implementations;
    private JPanel panel_report;
    private JSplitPane splitpanel_reports;
    private JPanel panel_leftReport;
    private JPanel panel_rightReport;
    private JTextPane txtpanel_formula;
    private JTextField txt_sicstusGeneratedCode;
    private JTextField txt_sicstusGeneratedReport;
    private JTextField txt_workflowFile;
    private JTextField txt_specificationFolder;
    private JTextField txt_z3GeneratedCode;
    private JTextField txt_z3GeneratedReport;

    public MainWindow(VerificationController verificationController, VerificationParameters verificationParameters) {
        super(verificationController, verificationParameters);
        $$$setupUI$$$();
        btn_parameters.addActionListener(new BtnParametersListener());
        cbx_specificationFile.addItemListener(new CbxSpecificationFileListener(verificationController));
        for (ESicstusImplementation sicstusImplementation : ESicstusImplementation.values()) {
            cbx_sicstusImplementations.addItem(sicstusImplementation);
        }
        for (EZ3Implementation z3Implementation : EZ3Implementation.values()) {
            cbx_z3Implementations.addItem(z3Implementation);
        }
    }

    @Override
    public void display() {
        final JFrame frame = new JFrame("Workflows Modal Specifications Verifier");
        frame.setContentPane(tab_main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    @Override
    public void verificationFolderChanged(VerificationParametersChanged event) {
        VerificationFolder verificationFolder = ((VerificationParameters) event.getSource()).getVerificationFolder();
        if (verificationFolder.isValid()) {
            txt_verificationFolder.setText(verificationFolder.getAbsolutePath());
            cbx_specificationFile.removeAllItems();
            for (SpecificationFile specificationFile : verificationFolder.getSpecificationFolder().getSpecificationFiles()) {
                cbx_specificationFile.addItem(new SpecificationItem(specificationFile));
            }
            txt_workflowFile.setText(verificationFolder.getPetriNetFile().getAbsolutePath());
        }
    }

    @Override
    public void specificationFileChanged(VerificationParametersChanged event) {
        VerificationFolder verificationFolder = ((VerificationParameters) event.getSource()).getVerificationFolder();
        SpecificationFile specificationFile = ((VerificationParameters) event.getSource()).getSpecificationFile();
        if (verificationFolder.isValid()) {
            Specification specification = specificationFile.extractSpecification();
            txt_specificationFolder.setText(verificationFolder.getSpecificationFolder().getAbsolutePath());
            txtpanel_formula.setText(specification.getFormula().toString());
            if (specification.getType() == SpecificationType.MAY) {
                radio_may.setSelected(true);
            } else {
                radio_must.setSelected(true);
            }
            txt_sicstusGeneratedCode.setText(new SicstusGeneratedCodeFile(verificationFolder, specificationFile).getAbsolutePath());
            txt_z3GeneratedCode.setText(new Z3GeneratedCodeFile(verificationFolder, specificationFile).getAbsolutePath());
            txt_sicstusGeneratedReport.setText(new SicstusGeneratedReportFile(verificationFolder, specificationFile).getAbsolutePath());
            txt_z3GeneratedReport.setText(new Z3GeneratedReportFile(verificationFolder, specificationFile).getAbsolutePath());
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        tab_main = new JTabbedPane();
        tab_main.setName("sdf");
        panel_verification = new JPanel();
        panel_verification.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        tab_main.addTab("Verification", panel_verification);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel_verification.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane1.setViewportView(panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 3, new Insets(2, 2, 2, 2), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(null, "Workspace", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        final JLabel label1 = new JLabel();
        label1.setText("Verification folder :");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txt_verificationFolder = new JTextField();
        txt_verificationFolder.setEditable(false);
        panel2.add(txt_verificationFolder, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btn_verificationFolder = new JButton();
        btn_verificationFolder.setText("...");
        panel2.add(btn_verificationFolder, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Generated report :");
        panel2.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txt_sicstusGeneratedReport = new JTextField();
        txt_sicstusGeneratedReport.setEditable(false);
        panel2.add(txt_sicstusGeneratedReport, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txt_sicstusGeneratedCode = new JTextField();
        txt_sicstusGeneratedCode.setEditable(false);
        panel2.add(txt_sicstusGeneratedCode, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Generated code :");
        panel2.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 3, new Insets(2, 2, 2, 2), -1, -1));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(null, "Specification", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        cbx_specificationFile = new JComboBox();
        panel3.add(cbx_specificationFile, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Name :");
        panel3.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setHorizontalScrollBarPolicy(30);
        panel3.add(scrollPane2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane2.setViewportView(panel4);
        final JLabel label5 = new JLabel();
        label5.setText("Formula :");
        panel3.add(label5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 1, new Insets(2, 2, 2, 2), -1, -1));
        panel3.add(panel5, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder(null, "Type", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        radio_may = new JRadioButton();
        radio_may.setSelected(true);
        radio_may.setText("May");
        panel5.add(radio_may, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        radio_must = new JRadioButton();
        radio_must.setText("Must");
        panel5.add(radio_must, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 2, new Insets(2, 2, 2, 2), -1, -1));
        panel1.add(panel6, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder(null, "Verification", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(2, 2, new Insets(2, 2, 2, 2), -1, -1));
        panel6.add(panel7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel7.setBorder(BorderFactory.createTitledBorder(null, "Sicstus", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        final JLabel label6 = new JLabel();
        label6.setText("Implementation :");
        panel7.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbx_sicstusImplementations = new JComboBox();
        panel7.add(cbx_sicstusImplementations, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_sicstusVerifySpecification = new JButton();
        btn_sicstusVerifySpecification.setText("Verify specification");
        panel7.add(btn_sicstusVerifySpecification, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(2, 2, new Insets(2, 2, 2, 2), -1, -1));
        panel6.add(panel8, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel8.setBorder(BorderFactory.createTitledBorder(null, "Z3", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        final JLabel label7 = new JLabel();
        label7.setText("Implementation :");
        panel8.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbx_z3Implementations = new JComboBox();
        panel8.add(cbx_z3Implementations, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_z3VerifySpecification = new JButton();
        btn_z3VerifySpecification.setText("Verify specification");
        panel8.add(btn_z3VerifySpecification, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_parameters = new JButton();
        btn_parameters.setText("Parameters...");
        panel6.add(btn_parameters, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel_report = new JPanel();
        panel_report.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel_report.setVisible(true);
        tab_main.addTab("Reports", panel_report);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel_report.add(scrollPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane3.setViewportView(panel9);
        splitpanel_reports = new JSplitPane();
        splitpanel_reports.setOneTouchExpandable(true);
        splitpanel_reports.setResizeWeight(0.5);
        panel9.add(splitpanel_reports, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        panel_leftReport = new JPanel();
        panel_leftReport.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitpanel_reports.setLeftComponent(panel_leftReport);
        panel_leftReport.setBorder(BorderFactory.createTitledBorder("May validity report"));
        panel_rightReport = new JPanel();
        panel_rightReport.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitpanel_reports.setRightComponent(panel_rightReport);
        panel_rightReport.setBorder(BorderFactory.createTitledBorder("Must validity report"));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(radio_may);
        buttonGroup.add(radio_may);
        buttonGroup.add(radio_must);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return tab_main;
    }

}
