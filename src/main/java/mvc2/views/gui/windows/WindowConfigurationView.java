package mvc2.views.gui.windows;

import codegeneration.implementations.sicstus.ESicstusImplementations;
import codegeneration.implementations.z3.EZ3Implementations;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import files.SpecificationFile;
import files.VerificationFolder;
import mvc2.controller.Controller;
import mvc2.events.events.*;
import mvc2.views.AbstractConfigurationView;
import mvc2.views.gui.items.SicstusImplementationItem;
import mvc2.views.gui.items.SpecificationItem;
import mvc2.views.gui.items.Z3ImplementationItem;
import mvc2.views.gui.listeners.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class WindowConfigurationView extends AbstractConfigurationView {

    private JFrame frame;
    private JTabbedPane tab_main;

    private JPanel panel_verification;
    private JTextField txt_verificationFolder;
    private JButton btn_verificationFolder;
    private JComboBox<SpecificationItem> cbx_specificationFile;
    private JRadioButton radio_may;
    private JRadioButton radio_must;
    private JButton btn_parameters;
    private JButton btn_sicstusVerifySpecification;
    private JComboBox<SicstusImplementationItem> cbx_sicstusImplementation;
    private JButton btn_z3VerifySpecification;
    private JComboBox<Z3ImplementationItem> cbx_z3Implementation;
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
    private JEditorPane editpanel_mayOverApproximation2;
    private JEditorPane editpanel_mayOverApproximation1;
    private JEditorPane editpanel_mayOverApproximation3;
    private JEditorPane editpanel_mayUnderApproximation;
    private JEditorPane editpanel_mustOverApproximation1;
    private JEditorPane editpanel_mustOverApproximation2;
    private JEditorPane editpanel_mustOverApproximation3;
    private JEditorPane editpanel_mustUnderApproximation;

    public WindowConfigurationView(Controller controller) {
        super(controller);
        $$$setupUI$$$();
        addEventListeners();
        buildFrame();
    }

    private void buildFrame() {
        for (ESicstusImplementations sicstusImplementation : ESicstusImplementations.values()) {
            cbx_sicstusImplementation.addItem(new SicstusImplementationItem(sicstusImplementation));
        }
        for (EZ3Implementations z3Implementation : EZ3Implementations.values()) {
            cbx_z3Implementation.addItem(new Z3ImplementationItem(z3Implementation));
        }
        frame = new JFrame("Modal Specifications Checker");
        frame.setContentPane(tab_main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    private void addEventListeners() {
        btn_verificationFolder.addActionListener(new BtnVerificationFolderListener(controller));
        cbx_specificationFile.addItemListener(new CbxSpecificationFileListener(controller));
        btn_parameters.addActionListener(new BtnParametersListener(controller));
        cbx_sicstusImplementation.addItemListener(new CbxSicstusImplementationListener(controller));
        cbx_z3Implementation.addItemListener(new CbxZ3ImplementationListener(controller));
        btn_sicstusVerifySpecification.addActionListener(new BtnSicstusVerifySpecificationListener(controller));
        btn_z3VerifySpecification.addActionListener(new BtnZ3VerifySpecificationListener(controller));
    }

    @Override
    public void display() {
        frame.setVisible(true);
    }

    /*****************************************************/
    /** EVENTS *******************************************/
    /*****************************************************/

    @Override
    public void verificationFolderChanged(VerificationFolderChanged verificationFolderChanged) {
        VerificationFolder verificationFolder = verificationFolderChanged.getVerificationFolder();
        txt_verificationFolder.setText(verificationFolder.getAbsolutePath());
        txt_workflowFile.setText(verificationFolder.getWorkflowFile().getAbsolutePath());
        txt_specificationFolder.setText(verificationFolder.getSpecificationFolder().getAbsolutePath());
        cbx_specificationFile.removeAllItems();
        for (SpecificationFile specificationFile : verificationFolder.getSpecificationFolder().getSpecificationFiles()) {
            cbx_specificationFile.addItem(new SpecificationItem(specificationFile));
        }
    }

    @Override
    public void specificationFileChanged(SpecificationFileChanged specificationFileChanged) {
        SpecificationFile specificationFile = specificationFileChanged.getSpecificationFile();
        txtpanel_formula.setText(specificationFile.extractSpecification().getFormula().toString());
        switch (specificationFile.extractSpecification().getType()) {
            case MAY:
                radio_may.setSelected(true);
                break;
            case MUST:
                radio_must.setSelected(true);
                break;
        }
    }

    @Override
    public void sicstusGeneratedCodeFilesChanged(SicstusGeneratedFilesChanged sicstusGeneratedFilesChanged) {
        txt_sicstusGeneratedCode.setText(sicstusGeneratedFilesChanged.getSicstusGeneratedCodeFile().getAbsolutePath());
        txt_sicstusGeneratedReport.setText(sicstusGeneratedFilesChanged.getSicstusGeneratedReportFile().getAbsolutePath());
    }

    @Override
    public void z3GeneratedCodeFilesChanged(Z3GeneratedFilesChanged z3GeneratedFilesChanged) {
        txt_z3GeneratedCode.setText(z3GeneratedFilesChanged.getZ3GeneratedCodeFile().getAbsolutePath());
        txt_z3GeneratedReport.setText(z3GeneratedFilesChanged.getZ3GeneratedReportFile().getAbsolutePath());
    }

    @Override
    public void verificationStarted(VerificationStarted verificationStarted) {
        editpanel_mayOverApproximation1.setText("");
        editpanel_mayOverApproximation2.setText("");
        editpanel_mayOverApproximation3.setText("");
        editpanel_mayUnderApproximation.setText("");
        editpanel_mustOverApproximation1.setText("");
        editpanel_mustOverApproximation2.setText("");
        editpanel_mustOverApproximation3.setText("");
        editpanel_mustUnderApproximation.setText("");
        tab_main.setSelectedComponent(panel_report);
    }

    @Override
    public void doneChecking(DoneChecking doneChecking) {
        JEditorPane editorPane = null;
        switch (doneChecking.getReport().getImplementation().getSpecification().getType()) {
            case MAY:
                switch (doneChecking.getReport().getApproximationType()) {
                    case OVER_APPROXIMATION_1:
                        editorPane = editpanel_mayOverApproximation1;
                        break;
                    case OVER_APPROXIMATION_2:
                        editorPane = editpanel_mayOverApproximation2;
                        break;
                    case OVER_APPROXIMATION_3:
                        editorPane = editpanel_mayOverApproximation3;
                        break;
                    case UNDER_APPROXIMATION:
                        editorPane = editpanel_mayUnderApproximation;
                        break;
                }
                break;
            case MUST:
                switch (doneChecking.getReport().getApproximationType()) {
                    case OVER_APPROXIMATION_1:
                        editorPane = editpanel_mustOverApproximation1;
                        break;
                    case OVER_APPROXIMATION_2:
                        editorPane = editpanel_mustOverApproximation2;
                        break;
                    case OVER_APPROXIMATION_3:
                        editorPane = editpanel_mustOverApproximation3;
                        break;
                    case UNDER_APPROXIMATION:
                        editorPane = editpanel_mustUnderApproximation;
                        break;
                }
                break;
        }
        editorPane.setText(doneChecking.getReport().toString());
    }

    /*****************************************************/

    private void createUIComponents() {
        // TODO: place custom component creation code here
        splitpanel_reports = new JSplitPane();
        splitpanel_reports.setDividerLocation(0.5);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        tab_main = new JTabbedPane();
        tab_main.setName("sdf");
        tab_main.setTabLayoutPolicy(0);
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
        radio_may.setSelected(false);
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
        panel_report.setVisible(true);
        tab_main.addTab("Reports", panel_report);
        splitpanel_reports.setOneTouchExpandable(true);
        splitpanel_reports.setResizeWeight(0.5);
        panel_report.add(splitpanel_reports, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        panel_leftReport = new JPanel();
        panel_leftReport.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitpanel_reports.setLeftComponent(panel_leftReport);
        panel_leftReport.setBorder(BorderFactory.createTitledBorder(null, "May validity report", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel_leftReport.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane2.setBorder(BorderFactory.createTitledBorder(null, "Over-approximation 1", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        editpanel_mayOverApproximation1 = new JEditorPane();
        scrollPane2.setViewportView(editpanel_mayOverApproximation1);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel_leftReport.add(scrollPane3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane3.setBorder(BorderFactory.createTitledBorder(null, "Over-approximation 2", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        editpanel_mayOverApproximation2 = new JEditorPane();
        scrollPane3.setViewportView(editpanel_mayOverApproximation2);
        final JScrollPane scrollPane4 = new JScrollPane();
        panel_leftReport.add(scrollPane4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane4.setBorder(BorderFactory.createTitledBorder(null, "Over-approximation 3", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        editpanel_mayOverApproximation3 = new JEditorPane();
        scrollPane4.setViewportView(editpanel_mayOverApproximation3);
        final JScrollPane scrollPane5 = new JScrollPane();
        panel_leftReport.add(scrollPane5, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane5.setBorder(BorderFactory.createTitledBorder(null, "Under-approximation", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        editpanel_mayUnderApproximation = new JEditorPane();
        scrollPane5.setViewportView(editpanel_mayUnderApproximation);
        panel_rightReport = new JPanel();
        panel_rightReport.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitpanel_reports.setRightComponent(panel_rightReport);
        panel_rightReport.setBorder(BorderFactory.createTitledBorder(null, "Must validity report", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel_rightReport.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane6 = new JScrollPane();
        panel7.add(scrollPane6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane6.setBorder(BorderFactory.createTitledBorder(null, "Over-approximation 1", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        editpanel_mustOverApproximation1 = new JEditorPane();
        scrollPane6.setViewportView(editpanel_mustOverApproximation1);
        final JScrollPane scrollPane7 = new JScrollPane();
        panel7.add(scrollPane7, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane7.setBorder(BorderFactory.createTitledBorder(null, "Under-approximation", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        editpanel_mustUnderApproximation = new JEditorPane();
        scrollPane7.setViewportView(editpanel_mustUnderApproximation);
        final JScrollPane scrollPane8 = new JScrollPane();
        panel7.add(scrollPane8, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane8.setBorder(BorderFactory.createTitledBorder(null, "Over-approximation 3", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        editpanel_mustOverApproximation3 = new JEditorPane();
        scrollPane8.setViewportView(editpanel_mustOverApproximation3);
        final JScrollPane scrollPane9 = new JScrollPane();
        panel7.add(scrollPane9, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane9.setBorder(BorderFactory.createTitledBorder(null, "Over-approximation 2", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        editpanel_mustOverApproximation2 = new JEditorPane();
        scrollPane9.setViewportView(editpanel_mustOverApproximation2);
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
