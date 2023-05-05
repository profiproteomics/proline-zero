package fr.proline.zero.gui;


import fr.proline.studio.gui.DefaultDialog;

import fr.proline.studio.utils.IconManager;


import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.ParsingRule;
import fr.proline.zero.util.ParsingRulesTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
;

public class ParsingRuleEditDialog extends DefaultDialog {
    private JTextField labelField;
    private JTextField fastaVersionTField;
    private JTextField proteinAccTField;
    private JTextField fastaNameTField;
    private List<String> fastaList;
    private JButton removeFastaNameRuleJButton;
    private JTextField lineField;
    private JTextField resultOfTest;
    private DefaultTableModel fastaNamesTableModel;
    private JTable fastaNamesTable;
    private static String deleteColummnIdentifier = "      ";
    private static final String[] columns = {"Rule", deleteColummnIdentifier};
    private static final Color jTableColor = new Color(174, 182, 222);

    private static final Color testTableColor = new Color(200, 200, 200);

    private TypeOfDialog typeOfDialog;

    private static final Logger LOG = LoggerFactory.getLogger(ParsingRuleEditDialog.class);

    enum TypeOfDialog {Add, Edit, ViewFastas}


    public ParsingRuleEditDialog(Window Parent, TypeOfDialog typeOfDialog, ParsingRule parsingRule) {

        super(Parent);

        //Configure commons buttons for all TypeOfDialog
        this.setButtonVisible(BUTTON_HELP, false);

        // Test button not used anymore
        this.setButtonName(BUTTON_DEFAULT, "Test");
        this.setButtonVisible(BUTTON_DEFAULT, false);
        this.setButtonEnabled(BUTTON_DEFAULT, false);
        this.setButtonIcon(BUTTON_DEFAULT, IconManager.getIcon(IconManager.IconType.TEST));

        this.setButtonVisible(BUTTON_BACK, true);
        this.setButtonName(BUTTON_BACK, "Clear");
        this.setButtonIcon(BUTTON_BACK, IconManager.getIcon(IconManager.IconType.ERASER));


        this.setButtonIcon(BUTTON_OK, IconManager.getIcon(IconManager.IconType.PLUS_16X16));
        this.typeOfDialog = typeOfDialog;

        if (typeOfDialog.equals(TypeOfDialog.Add)) {
            this.setButtonName(BUTTON_OK, "Add");
            this.setIconImage(IconManager.getImage(IconManager.IconType.PLUS_16X16));
            this.setTitle("Add Parsing Rule");

        }
        if (typeOfDialog.equals(TypeOfDialog.Edit)) {
            this.setIconImage(IconManager.getImage(IconManager.IconType.EDIT));
            this.setButtonName(BUTTON_OK, "Update");
            this.setTitle("Edit Parsing Rule");

        }
        if (!typeOfDialog.equals(TypeOfDialog.ViewFastas)) {
            JPanel internalPanel = createParsingRulesJPanel(parsingRule);
            setStatusVisible(false);
            setInternalComponent(internalPanel);


        }

        else {

        }
        this.setStatusVisible(true);
        this.setResizable(true);

        super.pack();


    }


    private JPanel createParsingRulesJPanel(ParsingRule parsingRuleEdited) {
        // creation du panel et du layout
        JPanel addParsingRules = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new java.awt.Insets(9, 5, 5, 5);
        // creation des elements

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill=GridBagConstraints.BOTH;
        c.weightx=1;
        addParsingRules.add(newParsingRulePanel(), c);

        c.insets = new java.awt.Insets(3, 5, 5, 5);
        JPanel fastaNamepanel=newFastaNamePanel();

        // JtextFields are filled with previous values if parsing rule is edited
        if (typeOfDialog.equals(ParsingRuleEditDialog.TypeOfDialog.Edit)) {
            labelField.setText(parsingRuleEdited.getName());
            fastaVersionTField.setText(parsingRuleEdited.getFastaVersionRegExp());
            proteinAccTField.setText(parsingRuleEdited.getProteinAccRegExp());
            List<String> fastaNames = parsingRuleEdited.getFastaNameRegExp();

            fastaList = fastaNames;
            // draws the jTable to be modified
            for (int k = 0; k < fastaNames.size(); k++) {
                Object[] vector = {fastaNames.get(k), removeFastaNameRuleJButton};
                fastaNamesTableModel.addRow(vector);
            }
            c.weighty=0;

        }
        c.insets = new java.awt.Insets(3, 5, 5, 5);
        c.gridx++;
       // c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.WEST;
        c.gridheight=2;
        c.weightx=0;
        addParsingRules.add(fastaNamepanel, c);
        c.gridx=0;
        c.gridy=1;
        c.gridheight=1;
        c.fill=GridBagConstraints.BOTH;
        c.weightx=1;
        addParsingRules.add(createTestPanel(),c);
        addParsingRules.add(Box.createHorizontalGlue(), c);

        return addParsingRules;


    }

    private JPanel newParsingRulePanel() {
        JPanel parsingPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        parsingPanel.setBorder(BorderFactory.createTitledBorder(""));

        JLabel labelName = new JLabel("Label: ");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.SOUTHEAST;
        constraints.weightx = 0;
        parsingPanel.add(labelName, constraints);

        labelField = new JTextField();
        constraints.gridx++;
        labelField.setPreferredSize(new Dimension(150, 20));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        parsingPanel.add(labelField, constraints);

        constraints.gridy++;
        JLabel fastaVersion = new JLabel("Fasta Version Rule: ");
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.SOUTHEAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        parsingPanel.add(fastaVersion, constraints);

        fastaVersionTField = new JTextField();
        constraints.gridx++;
        fastaVersionTField.setPreferredSize(new Dimension(150, 20));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        parsingPanel.add(fastaVersionTField, constraints);

        constraints.gridy++;
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.SOUTHEAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        JLabel protlabel = new JLabel("Protein Accession Rule: ");
        parsingPanel.add(protlabel, constraints);

        proteinAccTField = new JTextField();
        constraints.gridx++;
        proteinAccTField.setPreferredSize(new Dimension(150, 20));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        parsingPanel.add(proteinAccTField, constraints);

        return parsingPanel;
    }

    private JPanel newFastaNamePanel() {
        JPanel fastaPanel = new JPanel(new GridBagLayout());

        fastaPanel.setBorder(BorderFactory.createTitledBorder("Add Fasta Name Rules: "));
        GridBagConstraints parsingConstraints = new GridBagConstraints();
        parsingConstraints.insets = new Insets(5, 5, 5, 5);

        parsingConstraints.gridy = 0;
        parsingConstraints.gridx = 0;
        parsingConstraints.fill = GridBagConstraints.BOTH;

        fastaList = new ArrayList<>();
        JButton addButton = new JButton(IconManager.getIcon(IconManager.IconType.PLUS_16X16));
        addButton.setToolTipText("Click to add fasta name rule entered above");
        addButton.addActionListener(e -> {
            addFastaNames();
        });


        fastaNameTField = new JTextField();
        parsingConstraints.anchor = GridBagConstraints.NORTHWEST;
        parsingConstraints.weightx = 1;
        fastaNameTField.setPreferredSize(new Dimension(170, 23));
        fastaPanel.add(fastaNameTField, parsingConstraints);
        parsingConstraints.gridx++;
        parsingConstraints.weightx = 0;
        parsingConstraints.fill = GridBagConstraints.NONE;
        parsingConstraints.anchor = GridBagConstraints.EAST;
        fastaPanel.add(addButton, parsingConstraints);
        parsingConstraints.gridx = 0;
        parsingConstraints.gridy++;
        parsingConstraints.anchor = GridBagConstraints.WEST;
        fastaPanel.add(new JLabel("Fasta Name Rules: "), parsingConstraints);

        fastaNamesTableModel = new DefaultTableModel();
        fastaNamesTableModel.setColumnIdentifiers(columns);
        fastaNamesTable = new JTable();

        removeFastaNameRuleJButton = new JButton();
        fastaNamesTable.setModel(fastaNamesTableModel);
        fastaNamesTable.setGridColor(jTableColor);
        fastaNamesTable.setRowHeight(20);
        fastaNamesTable.setShowGrid(true);
        fastaNamesTable.setIntercellSpacing(new Dimension(2, 2));
        fastaNamesTable.setDefaultRenderer(Object.class, new CustomRenderer());

        // fastaNamesTable.getTableHeader().setDefaultRenderer(new SimpleHeaderRenderer());


        fastaNamesTable.getColumn(deleteColummnIdentifier).setCellRenderer(new TableButtonRenderer());
        fastaNamesTable.getColumn(deleteColummnIdentifier).setCellEditor(new TableButtonEditor(new JCheckBox()));
        fastaNamesTable.getColumn(deleteColummnIdentifier).setMaxWidth(40);
        JScrollPane scrollPane = new JScrollPane(fastaNamesTable);

        //scrollPane.setPreferredSize(new Dimension(new Dimension(110, 100)));

        parsingConstraints.gridy++;
        parsingConstraints.gridwidth = 2;
        parsingConstraints.gridx = 0;
        parsingConstraints.weightx = 1;
        parsingConstraints.weighty=1;
        parsingConstraints.fill = GridBagConstraints.BOTH;
        fastaPanel.add(scrollPane, parsingConstraints);


        return fastaPanel;


    }
   /* private JPanel testPanel(){
        JPanel testPanel=new JPanel(new GridBagLayout());
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(5,5,5,5);
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.anchor=GridBagConstraints.WEST;
        gbc.fill=GridBagConstraints.NONE;
        JLabel entryLineLabel=new JLabel("Fasta entry");
        testPanel.setBorder(BorderFactory.createTitledBorder("Test"));
        testPanel.add(entryLineLabel,gbc);

        return testPanel;


    }*/
    private JPanel createTestPanel() {
        JPanel testPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        testPanel.setBorder(BorderFactory.createTitledBorder("Test"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 2, 5, 2);

        JLabel entryLineLabel = new JLabel("Fasta entry: ");
        gbc.anchor=GridBagConstraints.WEST;
        gbc.fill=GridBagConstraints.NONE;
        gbc.weightx=0;

        testPanel.add(entryLineLabel, gbc);
        gbc.gridx++;

        lineField = new JTextField();
        lineField.setEditable(true);
        lineField.setEnabled(true);
        lineField.setToolTipText("Enter a line from a fasta file");
        gbc.weightx = 1;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        testPanel.add(lineField, gbc);
        JButton testButton = new JButton("Test");
        testButton.setToolTipText("Click to test default protein accesion rule:  "+proteinAccTField.getText());

        testButton.addActionListener(e -> {

            String lineTested = lineField.getText();
            String protRegex = proteinAccTField.getText();

            if (!lineTested.equals("")) {
                String proteinNameExtracted = ParsingRulesTester.extractProteinNameWithRegEx(lineTested, protRegex);
                if (proteinNameExtracted != null) {
                    resultOfTest.setText(proteinNameExtracted);
                    revalidate();
                    repaint();
                } else {
                    resultOfTest.setText("No protein name extracted");
                    revalidate();
                    repaint();
                }
            } else {
                highlight(lineField);
                setStatus(true, "Please enter a line from a fasta file ");
            }
        });
        gbc.gridx++;
        gbc.fill = GridBagConstraints.NONE;
        testButton.setMargin(new Insets(3,3,3,3));
        gbc.anchor=GridBagConstraints.EAST;
        gbc.weightx=0;
        testPanel.add(testButton, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor=GridBagConstraints.WEST;
        gbc.weightx=0;
        testPanel.add(new JLabel("protein name extracted :"), gbc);
        resultOfTest = new JTextField();
        resultOfTest.setEnabled(true);
        resultOfTest.setEditable(true);
        gbc.gridx++;

        gbc.weightx = 1;
        gbc.gridwidth=2;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.anchor=GridBagConstraints.EAST;
        testPanel.add(resultOfTest, gbc);
        return testPanel;
    }

  /*  private JPanel viewFastaNamePanel(ParsingRule parsingRule) {
        JPanel fastaPanel = new JPanel(new GridBagLayout());

        // fastaPanel.setBorder(BorderFactory.createTitledBorder("View Fasta Name Rules: "));
        GridBagConstraints parsingConstraints = new GridBagConstraints();
        parsingConstraints.insets = new Insets(5, 5, 5, 5);

        parsingConstraints.gridy = 0;
        parsingConstraints.gridx = 0;
        parsingConstraints.fill = GridBagConstraints.HORIZONTAL;


        // parsingConstraints.gridy++;
        parsingConstraints.anchor = GridBagConstraints.WEST;
        fastaPanel.add(new JLabel("Fasta Name Rules: "), parsingConstraints);

        fastaNamesTableModel = new DefaultTableModel();
        fastaNamesTableModel.setColumnIdentifiers(columns);
        fastaNamesTable = new JTable();

        removeFastaNameRuleJButton = new JButton();

        fastaNamesTable.setModel(fastaNamesTableModel);
        fastaNamesTable.setGridColor(jTableColor);
        fastaNamesTable.setRowHeight(20);
        fastaNamesTable.setShowGrid(true);
        fastaNamesTable.setIntercellSpacing(new Dimension(2, 2));
        fastaNamesTable.setDefaultRenderer(Object.class, new CustomRenderer());

        fastaNamesTable.getTableHeader().setDefaultRenderer(new SimpleHeaderRenderer());
        fastaNamesTable.getColumn(deleteColummnIdentifier).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> null);
        fastaNamesTable.getColumn(deleteColummnIdentifier).setCellEditor(null);
        fastaNamesTable.getColumn(deleteColummnIdentifier).setMaxWidth(40);
        fastaList = parsingRule.getFastaNameRegExp();
        for (int k = 0; k < fastaList.size(); k++) {
            Object[] vector = {fastaList.get(k), null};
            fastaNamesTableModel.addRow(vector);
        }
        JScrollPane scrollPane = new JScrollPane(fastaNamesTable);

        scrollPane.setPreferredSize(new Dimension(new Dimension(110, 100)));

        parsingConstraints.gridy++;
        parsingConstraints.gridwidth = 2;
        parsingConstraints.gridx = 0;
        parsingConstraints.weightx = 1;
        parsingConstraints.fill = GridBagConstraints.BOTH;
        fastaPanel.add(scrollPane, parsingConstraints);


        return fastaPanel;


    }*/


    @Override
    protected boolean okCalled() {
        System.out.println("Ok pressed");
        if (this.typeOfDialog.equals(TypeOfDialog.ViewFastas)) {
            return true;
        }
        boolean entriesAreValid = true;
        boolean formFullyFilled = !labelField.getText().isEmpty() && !proteinAccTField.getText().isEmpty()
                && !fastaVersionTField.getText().isEmpty() && !fastaList.isEmpty();
        boolean forgottenEntry = !fastaNameTField.getText().isEmpty();
        if (!formFullyFilled) {
            if (labelField.getText().isEmpty()) {
                highlight(labelField);
                setStatus(true, "Please add a label");
                entriesAreValid = false;
            } else if (proteinAccTField.getText().isEmpty()) {
                highlight(proteinAccTField);
                setStatus(true, "Please add a protein regex");
                entriesAreValid = false;
            } else if (fastaVersionTField.getText().isEmpty()) {
                highlight(fastaVersionTField);
                setStatus(true, "Please add a regex");
                entriesAreValid = false;
            } else if (fastaList.isEmpty()) {
                highlight(fastaNamesTable);
                setStatus(true, "Please add at least one fasta rule");
                entriesAreValid = false;
            }


        } else if (ConfigManager.getInstance().getParsingRulesManager().labelExists(labelField.getText()) && this.typeOfDialog.equals(TypeOfDialog.Add)) {
            highlight(labelField);
            setStatus(true, "Label already exists please choose another name ");
            entriesAreValid = false;
        } else if (forgottenEntry) {
            highlight(fastaNameTField);
            setStatus(false, "you might have forgotten an entry! ");
            // TODO popup should be deplaced
            String[] options = {"Delete", "Add"};
            boolean deleteOrAdd = Popup.optionYesNO("Do you want to add the value or delete it?", options);
            if (deleteOrAdd) {
                fastaNameTField.setText("");

            } else {
                addFastaNames();
            }
            entriesAreValid = false;

        }
        return entriesAreValid;
    }

    public ParsingRule getParsingRuleInsideDialog() {

        ParsingRule parsingrule = new ParsingRule(labelField.getText().trim(), fastaList, fastaVersionTField.getText().trim(), proteinAccTField.getText().trim());
        return parsingrule;
    }

    // close The dialog
    protected boolean cancelCalled() {

        System.out.println("Cancel pressed");

        return true;
    }

    // used to open local test dialog
    @Override
    protected boolean defaultCalled() {
        System.out.println("test pressed");

        if (proteinAccTField.getText().equals("")) {
            highlight(proteinAccTField);
            setStatus(true, "Regular expression missing");
        } else {
            TestParsingRuleDialog testDialog = new TestParsingRuleDialog(ConfigWindow.getInstance(), labelField, fastaVersionTField, proteinAccTField, fastaList);
            testDialog.centerToScreen();
            testDialog.setSize(800, 300);
            testDialog.setVisible(true);


            if (testDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {
                String newProteinRegExp = testDialog.getNewProteinAccessionRule();
                boolean proteinHasBeenChanged = !newProteinRegExp.equals(proteinAccTField.getText());
                if (proteinHasBeenChanged) {

                    String[] options = {"Keep", "Change"};
                    boolean userChooseToKeepOldValue = Popup.optionYesNO("During the test you modified the value of the protein accession rule\n" +
                            "you have the possibility to change the value", options);
                    if (!userChooseToKeepOldValue) {
                        proteinAccTField.setText(newProteinRegExp);
                    }

                }


            }
        }

        return false;
    }


    // used to reset fields inside Dialog
    protected boolean backCalled() {

        // a popup to confirm action could be added?
        labelField.setText("");
        fastaVersionTField.setText("");
        proteinAccTField.setText("");
        fastaNameTField.setText("");

        for (int k = fastaList.size() - 1; k >= 0; k--) {
            fastaNamesTableModel.removeRow(k);

        }
        fastaList.clear();
        revalidate();
        repaint();

        return true;

    }

    private void addFastaNames() {

        String fastaToBeAdded = fastaNameTField.getText().trim();
        if (fastaToBeAdded.length() != 0) {
            fastaNameTField.setText("");
            fastaList.add(fastaToBeAdded);
            Object[] vector = {fastaToBeAdded, removeFastaNameRuleJButton};
            fastaNamesTableModel.addRow(vector);
            revalidate();
            repaint();
        } else {
            Popup.warning("Please enter value");
        }


    }


    //// Methods used To implement the table
    public class TableButtonRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            removeFastaNameRuleJButton.setIcon(IconManager.getIcon(IconManager.IconType.TRASH));
            removeFastaNameRuleJButton.setBackground(new Color(210, 210, 230));

            removeFastaNameRuleJButton.setToolTipText("Click to remove Rule");
            removeFastaNameRuleJButton.setSize(20, 20);
            removeFastaNameRuleJButton.setBorderPainted(false);

            return removeFastaNameRuleJButton;

        }

    }

    class CustomRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                c.setBackground(Color.LIGHT_GRAY);
            } else {

                if (row % 2 == 0) {
                    c.setBackground(new Color(213, 218, 241));
                } else {

                    c.setBackground(Color.WHITE);
                }
            }

            return c;
        }
    }


    public class TableButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;

        private boolean isPushed;


        public TableButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.addActionListener(e -> {
                getCellEditorValue();
                fireEditingStopped();
            });
        }


        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int indexToBeDeleted = fastaNamesTable.getSelectedRow();

                if (fastaNamesTable.isEditing()) {
                    fastaNamesTable.getCellEditor().stopCellEditing();
                }

                fastaNamesTableModel.removeRow(indexToBeDeleted);
                fastaList.remove(indexToBeDeleted);

            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    public class SimpleHeaderRenderer extends JLabel implements TableCellRenderer {

        public SimpleHeaderRenderer() {

            setFont(new Font("Courier", Font.PLAIN, 11));
            setForeground(Color.WHITE);
            setOpaque(true);
            setBackground(new Color(213, 24, 30));
            setBackground(jTableColor);
            setBorder(BorderFactory.createEtchedBorder());
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }

    }


}
