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
import mvc.controllers.ConfigurationController;
import mvc.eventsmanagement.events.configuration.*;
import mvc.model.ConfigurationModel;
import mvc.views.AbstractConfigurationView;
import mvc.views.gui.items.SpecificationItem;
import mvc.views.gui.listeners.configuration.*;
import specifications.model.Specification;
import specifications.model.SpecificationType;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class WindowConfigurationView extends AbstractConfigurationView {

    private JTabbedPane tab_main;

    private JPanel panel_verification;
    private JTextField txt_verificationFolder;
    private JButton btn_verificationFolder;
    private JComboBox<SpecificationItem> cbx_specificationFile;
    private JRadioButton radio_may;
    private JRadioButton radio_must;
    private JButton btn_parameters;
    private JButton btn_sicstusVerifySpecification;
    private JComboBox<ESicstusImplementation> cbx_sicstusImplementation;
    private JButton btn_z3VerifySpecification;
    private JComboBox<EZ3Implementation> cbx_z3Implementation;
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
    private JFrame frame;

    public WindowConfigurationView(ConfigurationController configurationController, ConfigurationModel configurationModel) {
        super(configurationController, configurationModel);
        frame = new JFrame("Workflows Modal Specifications AbstractVerifier");
        frame.setContentPane(tab_main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setResizable(false);
        btn_parameters.addActionListener(new BtnParametersListener(getConfigurationController()));
        cbx_specificationFile.addItemListener(new CbxSpecificationFileListener(getConfigurationController()));
        CbxSicstusImplementationListener cbxSicstusImplementationListener = new CbxSicstusImplementationListener(getConfigurationController());
        cbx_sicstusImplementation.addItemListener(cbxSicstusImplementationListener);
        CbxZ3ImplementationListener cbxZ3ImplementationListener = new CbxZ3ImplementationListener(getConfigurationController());
        cbx_z3Implementation.addItemListener(cbxZ3ImplementationListener);
        btn_sicstusVerifySpecification.addActionListener(new BtnSicstusVerifySpecificationListener(getConfigurationController()));
        btn_z3VerifySpecification.addActionListener(new BtnZ3VerifySpecificationListener(getConfigurationController()));
        for (ESicstusImplementation sicstusImplementation : ESicstusImplementation.values()) {
            cbx_sicstusImplementation.addItem(sicstusImplementation);
        }
        for (EZ3Implementation z3Implementation : EZ3Implementation.values()) {
            cbx_z3Implementation.addItem(z3Implementation);
        }
    }

    @Override
    public void buildView() {
        $$$setupUI$$$();
    }

    @Override
    public void display() {
        frame.setVisible(true);
    }

    @Override
    public void close() {
        frame.dispose();
    }

    @Override
    public void verificationFolderChanged(VerificationFolderChanged event) {
        VerificationFolder verificationFolder = event.getNewVerificationFolder();
        if (verificationFolder.isValid()) {
            txt_verificationFolder.setText(verificationFolder.getAbsolutePath());
            cbx_specificationFile.removeAllItems();
            for (SpecificationFile specificationFile : verificationFolder.getSpecificationFolder().getSpecificationFiles()) {
                cbx_specificationFile.addItem(new SpecificationItem(specificationFile));
            }
            txt_workflowFile.setText(verificationFolder.getWorkflowFile().getAbsolutePath());
            txt_specificationFolder.setText(verificationFolder.getSpecificationFolder().getAbsolutePath());
        }
    }

    @Override
    public void specificationFileChanged(SpecificationFileChanged event) {
        VerificationFolder verificationFolder = ((ConfigurationModel) event.getSource()).getVerificationFolder();
        SpecificationFile specificationFile = event.getNewSpecificationFile();
        ESicstusImplementation sicstusImplementation = ((ConfigurationModel) event.getSource()).getSicstusImplementation();
        EZ3Implementation z3Implementation = ((ConfigurationModel) event.getSource()).getZ3Implementation();
        if (verificationFolder.isValid()) {
            Specification specification = specificationFile.extractSpecification();
            txt_specificationFolder.setText(verificationFolder.getSpecificationFolder().getAbsolutePath());
            txtpanel_formula.setText(specification.getFormula().toString());
            if (specification.getType() == SpecificationType.MAY) {
                radio_may.setSelected(true);
            } else {
                radio_must.setSelected(true);
            }
            txt_sicstusGeneratedCode.setText(new SicstusGeneratedCodeFile(verificationFolder, specificationFile, sicstusImplementation).getAbsolutePath());
            txt_sicstusGeneratedReport.setText(new SicstusGeneratedReportFile(verificationFolder, specificationFile, sicstusImplementation).getAbsolutePath());
            txt_z3GeneratedCode.setText(new Z3GeneratedCodeFile(verificationFolder, specificationFile, z3Implementation).getAbsolutePath());
            txt_z3GeneratedReport.setText(new Z3GeneratedReportFile(verificationFolder, specificationFile, z3Implementation).getAbsolutePath());
        }
    }

    @Override
    public void sicstusImplementationChanged(SicstusImplementationChanged event) {
        VerificationFolder verificationFolder = ((ConfigurationModel) event.getSource()).getVerificationFolder();
        SpecificationFile specificationFile = ((ConfigurationModel) event.getSource()).getSpecificationFile();
        ESicstusImplementation sicstusImplementation = event.getNewSicstusImplementation();
        txt_sicstusGeneratedCode.setText(new SicstusGeneratedCodeFile(verificationFolder, specificationFile, sicstusImplementation).getAbsolutePath());
        txt_sicstusGeneratedReport.setText(new SicstusGeneratedReportFile(verificationFolder, specificationFile, sicstusImplementation).getAbsolutePath());
    }

    @Override
    public void z3ImplementationChanged(Z3ImplementationChanged event) {
        VerificationFolder verificationFolder = ((ConfigurationModel) event.getSource()).getVerificationFolder();
        SpecificationFile specificationFile = ((ConfigurationModel) event.getSource()).getSpecificationFile();
        EZ3Implementation z3Implementation = event.getNewZ3Implementation();
        txt_z3GeneratedCode.setText(new Z3GeneratedCodeFile(verificationFolder, specificationFile, z3Implementation).getAbsolutePath());
        txt_z3GeneratedReport.setText(new Z3GeneratedReportFile(verificationFolder, specificationFile, z3Implementation).getAbsolutePath());
    }

    @Override
    public void sicstusVerificationDone(SicstusVerificationDone event) {
    }

    @Override
    public void z3VerificationDone(Z3VerificationDone event) {
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
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
        panel_verification.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        tab_main.addTab("Verification", panel_verification);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 3, new Insets(2, 2, 2, 2), -1, -1));
        panel_verification.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(null, "Workspace", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        final JLabel label1 = new JLabel();
        label1.setText("Verification folder :");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txt_verificationFolder = new JTextField();
        txt_verificationFolder.setEditable(false);
        panel1.add(txt_verificationFolder, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btn_verificationFolder = new JButton();
        btn_verificationFolder.setText("...");
        panel1.add(btn_verificationFolder, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Workflow file :");
        panel1.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txt_workflowFile = new JTextField();
        txt_workflowFile.setEditable(false);
        panel1.add(txt_workflowFile, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Specification folder :");
        panel1.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txt_specificationFolder = new JTextField();
        txt_specificationFolder.setEditable(false);
        panel1.add(txt_specificationFolder, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 3, new Insets(2, 2, 2, 2), -1, -1));
        panel_verification.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(null, "Specification", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        cbx_specificationFile = new JComboBox();
        panel2.add(cbx_specificationFile, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Name :");
        panel2.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(30);
        panel2.add(scrollPane1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        txtpanel_formula = new JTextPane();
        txtpanel_formula.setEditable(false);
        txtpanel_formula.setEnabled(true);
        scrollPane1.setViewportView(txtpanel_formula);
        final JLabel label5 = new JLabel();
        label5.setText("Formula :");
        panel2.add(label5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(2, 2, 2, 2), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(null, "Type", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        radio_may = new JRadioButton();
        radio_may.setEnabled(true);
        radio_may.setSelected(true);
        radio_may.setText("May");
        radio_may.setMnemonic('A');
        radio_may.setDisplayedMnemonicIndex(1);
        panel3.add(radio_may, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        radio_must = new JRadioButton();
        radio_must.setEnabled(true);
        radio_must.setText("Must");
        radio_must.setMnemonic('U');
        radio_must.setDisplayedMnemonicIndex(1);
        panel3.add(radio_must, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 2, new Insets(2, 2, 2, 2), -1, -1));
        panel_verification.add(panel4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder(null, "Verification", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(4, 2, new Insets(2, 2, 2, 2), -1, -1));
        panel4.add(panel5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder(null, "Sicstus", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        final JLabel label6 = new JLabel();
        label6.setText("Implementation :");
        panel5.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbx_sicstusImplementation = new JComboBox();
        panel5.add(cbx_sicstusImplementation, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_sicstusVerifySpecification = new JButton();
        btn_sicstusVerifySpecification.setText("Verify specification with Sicstus");
        btn_sicstusVerifySpecification.setMnemonic('S');
        btn_sicstusVerifySpecification.setDisplayedMnemonicIndex(26);
        panel5.add(btn_sicstusVerifySpecification, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txt_sicstusGeneratedCode = new JTextField();
        txt_sicstusGeneratedCode.setEditable(false);
        txt_sicstusGeneratedCode.setText("");
        panel5.add(txt_sicstusGeneratedCode, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Generated code :");
        panel5.add(label7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Generated report :");
        panel5.add(label8, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txt_sicstusGeneratedReport = new JTextField();
        txt_sicstusGeneratedReport.setEditable(false);
        panel5.add(txt_sicstusGeneratedReport, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(4, 2, new Insets(2, 2, 2, 2), -1, -1));
        panel4.add(panel6, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder(null, "Z3", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        final JLabel label9 = new JLabel();
        label9.setText("Implementation :");
        panel6.add(label9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbx_z3Implementation = new JComboBox();
        panel6.add(cbx_z3Implementation, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_z3VerifySpecification = new JButton();
        btn_z3VerifySpecification.setText("Verify specification with Z3");
        btn_z3VerifySpecification.setMnemonic('Z');
        btn_z3VerifySpecification.setDisplayedMnemonicIndex(26);
        panel6.add(btn_z3VerifySpecification, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txt_z3GeneratedCode = new JTextField();
        txt_z3GeneratedCode.setEditable(false);
        panel6.add(txt_z3GeneratedCode, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Generated code :");
        panel6.add(label10, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Generated report :");
        panel6.add(label11, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txt_z3GeneratedReport = new JTextField();
        txt_z3GeneratedReport.setEditable(false);
        panel6.add(txt_z3GeneratedReport, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btn_parameters = new JButton();
        btn_parameters.setText("Parameters...");
        btn_parameters.setMnemonic('P');
        btn_parameters.setDisplayedMnemonicIndex(0);
        panel4.add(btn_parameters, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel_report = new JPanel();
        panel_report.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel_report.setVisible(false);
        tab_main.addTab("Reports", panel_report);
        splitpanel_reports = new JSplitPane();
        splitpanel_reports.setOneTouchExpandable(true);
        splitpanel_reports.setResizeWeight(0.5);
        panel_report.add(splitpanel_reports, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
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
