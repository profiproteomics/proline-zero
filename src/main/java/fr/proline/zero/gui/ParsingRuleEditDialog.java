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

/**
 * Extends DefaultDialog (Studio)
 * Used to edit a parsing Rule
 * Local test on the protein accession rule of the parsing rule viewed
 *
 * @see ConfigManager
 * @see ParsingRule
 * @see ParsingRulesTester
 */

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
    private static final String deleteColumnIdentifier = "      ";
    private static final String[] columns = {"Rule", deleteColumnIdentifier};

    private static final Icon fastaListValid = IconManager.getIcon(IconManager.IconType.TICK_CIRCLE);
    private static final Icon fastaListNotValid = IconManager.getIcon(IconManager.IconType.WARNING);

    private JLabel indicatorLabel;

    private static final Color J_TABLE_COLOR = new Color(174, 182, 222);

    private static final Color TEST_TABLE_COLOR = new Color(200, 200, 200);

    private final static Color WARNING_COLOR = new Color(145, 147, 154);
    private final TypeOfDialog typeOfDialog;

    private static final Logger LOG = LoggerFactory.getLogger(ParsingRuleEditDialog.class);

    /**
     * enum used to differentiate types of dialog
     */
    enum TypeOfDialog {Add, Edit}

    private ParsingRule editedParsingRule;

    public ParsingRuleEditDialog(Window Parent, TypeOfDialog typeOfDialog, ParsingRule parsingRule) {

        super(Parent);
        //Configure commons buttons for all TypeOfDialog
        this.setButtonVisible(BUTTON_HELP, false);
        this.setButtonVisible(BUTTON_DEFAULT, false);

        this.setButtonVisible(BUTTON_BACK, true);
        this.setButtonName(BUTTON_BACK, "Clear");
        this.setButtonIcon(BUTTON_BACK, IconManager.getIcon(IconManager.IconType.ERASER));


        this.typeOfDialog = typeOfDialog;

        if (typeOfDialog.equals(TypeOfDialog.Add)) {
            this.setButtonName(BUTTON_OK, "Add");
            this.setIconImage(IconManager.getImage(IconManager.IconType.PLUS_16X16));
            this.setButtonIcon(BUTTON_OK, IconManager.getIcon(IconManager.IconType.PLUS_16X16));
            this.setTitle("Add parsing rule");


        }
        if (typeOfDialog.equals(TypeOfDialog.Edit)) {
            this.setIconImage(IconManager.getImage(IconManager.IconType.EDIT));
            this.setButtonName(BUTTON_OK, "Update");
            // TODO size of icon too big create a small update icon 16*16
            this.setButtonIcon(BUTTON_OK, IconManager.getIcon(IconManager.IconType.UPDATE));
            this.setTitle("Edit parsing rule");
            this.editedParsingRule = parsingRule.clone();

        }

        JPanel internalPanel = createParsingRulesJPanel(editedParsingRule);
        setInternalComponent(internalPanel);

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
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        addParsingRules.add(newParsingRulePanel(), c);

        c.insets = new java.awt.Insets(3, 5, 5, 5);

        JPanel fastaNamePanel = newFastaNamePanel();

        // JtextFields are filled with previous values if parsing rule is edited
        if (typeOfDialog.equals(ParsingRuleEditDialog.TypeOfDialog.Edit)) {
            labelField.setText(parsingRuleEdited.getName());
            fastaVersionTField.setText(parsingRuleEdited.getFastaVersionRegExp());
            proteinAccTField.setText(parsingRuleEdited.getProteinAccRegExp());
            if (!ParsingRulesTester.isRegexValid(parsingRuleEdited.getProteinAccRegExp())) {

                changeJTextFieldAttributes(proteinAccTField, WARNING_COLOR, Color.WHITE, "this regular expression is not valid");
            }

            fastaList = parsingRuleEdited.getFastaNameRegExp();
            if (!ParsingRulesTester.testFastaListInsideDialog(fastaList)) {
                indicatorLabel.setIcon(fastaListNotValid);
            } else {
                indicatorLabel.setIcon(fastaListValid);

            }

            // draws the jTable to be modified
            for (String fastaName : fastaList) {
                Object[] vector = {fastaName, removeFastaNameRuleJButton};
                fastaNamesTableModel.addRow(vector);
            }
            c.weighty = 0;

        }
        // Add
        else {
            indicatorLabel.setIcon(null);
        }
        c.insets = new java.awt.Insets(3, 5, 5, 5);
        c.gridx++;
        c.anchor = GridBagConstraints.WEST;
        c.gridheight = 2;
        c.weightx = 0;
        addParsingRules.add(fastaNamePanel, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        addParsingRules.add(createTestPanel(), c);
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
        JLabel fastaVersion = new JLabel("Fasta version rule: ");
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
        JLabel protLabel = new JLabel("Protein accession rule: ");
        parsingPanel.add(protLabel, constraints);

        proteinAccTField = new JTextField();
        constraints.gridx++;
        proteinAccTField.setPreferredSize(new Dimension(150, 20));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        parsingPanel.add(proteinAccTField, constraints);

        return parsingPanel;
    }

    /**
     * Display the JTable with fasta name regular expressions
     */
    private JPanel newFastaNamePanel() {
        JPanel fastaPanel = new JPanel(new GridBagLayout());

        fastaPanel.setBorder(BorderFactory.createTitledBorder(" Add fasta name rules  "));
        GridBagConstraints parsingConstraints = new GridBagConstraints();
        parsingConstraints.insets = new Insets(5, 5, 5, 5);

        parsingConstraints.gridy = 0;
        parsingConstraints.gridx = 0;
        parsingConstraints.fill = GridBagConstraints.BOTH;

        fastaList = new ArrayList<>();
        JButton addButton = new JButton(IconManager.getIcon(IconManager.IconType.PLUS_16X16));
        addButton.setToolTipText("Click to add fasta name rule");
        addButton.addActionListener(e -> addFastaNames());

        fastaNameTField = new JTextField();
        parsingConstraints.anchor = GridBagConstraints.NORTHWEST;
        parsingConstraints.weightx = 1;
        //parsingConstraints.weighty=0;
        //parsingConstraints.gridheight=1;

        fastaNameTField.setPreferredSize(new Dimension(fastaNameTField.getPreferredSize()));

        fastaPanel.add(fastaNameTField, parsingConstraints);
        parsingConstraints.gridx++;
        parsingConstraints.weightx = 0;
        parsingConstraints.weighty = 0;

        parsingConstraints.fill = GridBagConstraints.NONE;
        parsingConstraints.anchor = GridBagConstraints.EAST;
        addButton.setPreferredSize(new Dimension(addButton.getPreferredSize()));
        fastaPanel.add(addButton, parsingConstraints);
        parsingConstraints.gridx = 0;
        parsingConstraints.gridy++;
        parsingConstraints.anchor = GridBagConstraints.WEST;
        fastaPanel.add(new JLabel("Fasta name rules: "), parsingConstraints);
        parsingConstraints.gridx++;
        indicatorLabel = new JLabel(fastaListValid);
        parsingConstraints.anchor = GridBagConstraints.CENTER;
        fastaPanel.add(indicatorLabel, parsingConstraints);
        parsingConstraints.anchor = GridBagConstraints.EAST;
        fastaNamesTableModel = new DefaultTableModel();
        fastaNamesTableModel.setColumnIdentifiers(columns);
        fastaNamesTable = new JTable();
        removeFastaNameRuleJButton = new JButton();
        fastaNamesTable.setModel(fastaNamesTableModel);
        fastaNamesTable.setGridColor(J_TABLE_COLOR);
        fastaNamesTable.setRowHeight(20);
        fastaNamesTable.setShowGrid(true);
        fastaNamesTable.setIntercellSpacing(new Dimension(2, 2));
        fastaNamesTable.setDefaultRenderer(Object.class, new CustomRenderer());
        fastaNamesTable.getColumn(deleteColumnIdentifier).setCellRenderer(new TableButtonRenderer());
        fastaNamesTable.getColumn(deleteColumnIdentifier).setCellEditor(new TableButtonEditor(new JCheckBox()));
        fastaNamesTable.getColumn(deleteColumnIdentifier).setMaxWidth(40);
        JScrollPane scrollPane = new JScrollPane(fastaNamesTable);
        parsingConstraints.gridy++;
        parsingConstraints.gridwidth = 2;
        parsingConstraints.gridx = 0;
        parsingConstraints.weightx = 1;
        parsingConstraints.weighty = 1;
        parsingConstraints.fill = GridBagConstraints.BOTH;
        fastaPanel.add(scrollPane, parsingConstraints);


        return fastaPanel;


    }

    private JPanel createTestPanel() {
        JPanel testPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        testPanel.setBorder(BorderFactory.createTitledBorder(" Test "));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 2, 5, 2);

        JLabel entryLineLabel = new JLabel("Fasta entry: ");
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        testPanel.add(entryLineLabel, gbc);
        gbc.gridx++;

        lineField = new JTextField();
        lineField.setEditable(true);
        lineField.setEnabled(true);
        lineField.setToolTipText("Enter a line from a fasta file");
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        testPanel.add(lineField, gbc);
        JButton testButton = new JButton("Test");
        testButton.setToolTipText("Click to test protein accession rule:  " + proteinAccTField.getText());

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
                    resultOfTest.setText("No protein accession extracted");
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
        testButton.setMargin(new Insets(3, 3, 3, 3));
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        testPanel.add(testButton, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        testPanel.add(new JLabel("Protein accession extracted: "), gbc);
        resultOfTest = new JTextField();
        resultOfTest.setEnabled(true);
        resultOfTest.setEditable(true);
        gbc.gridx++;

        gbc.weightx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        testPanel.add(resultOfTest, gbc);
        return testPanel;
    }

    private void addFastaNames() {

        String fastaToBeAdded = fastaNameTField.getText().trim();
        boolean isValid = ParsingRulesTester.isRegexValid(fastaToBeAdded);
        if (!isValid) {
            Popup.warning("the regular expression you filled is not valid");
        } else if (fastaToBeAdded.length() != 0) {
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

    /**
     * check if entries inside EditDialog are valid
     *
     * @return true if entries are all filled
     *Checks if label already exists and if regular expressions are valid
     */

    @Override
    protected boolean okCalled() {
        boolean entriesAreValid = true;
        boolean formFullyFilled = !labelField.getText().isEmpty() && !proteinAccTField.getText().isEmpty()
                && !fastaVersionTField.getText().isEmpty() && !fastaList.isEmpty();
        boolean forgottenEntry = !fastaNameTField.getText().isEmpty();
        boolean parsingRuleLabelDidChange = false;
        if (!labelField.getText().isEmpty() && typeOfDialog.equals(TypeOfDialog.Edit)) {
            parsingRuleLabelDidChange = !labelField.getText().equals(editedParsingRule.getName());
        }

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


        } else if (parsingRuleLabelDidChange || typeOfDialog.equals(TypeOfDialog.Add)) {
            if (ConfigManager.getInstance().getParsingRulesManager().labelExists(labelField.getText())) {
                highlight(labelField);
                setStatus(true, "Label already exists please choose another one ");
                entriesAreValid = false;
            }
        } else if (forgottenEntry) {
            highlight(fastaNameTField);
            setStatus(true, "you might have forgotten an entry! ");
            String[] options = {"Delete", "Add"};

            boolean deleteOrAdd = Popup.optionYesNoCenterToComponent(proteinAccTField, "Do you want to add the rule or delete it?", options);
            if (deleteOrAdd) {
                fastaNameTField.setText("");

            } else {
                addFastaNames();
            }
            entriesAreValid = false;

        }
        //at that point all entries are filled
        if (entriesAreValid) {
            boolean proteinRegExIsValid = ParsingRulesTester.isRegexProInsideDialog(proteinAccTField.getText().trim());
            if (!proteinRegExIsValid) {
                highlight(proteinAccTField);
                setStatus(true, "protein accession rule is not valid");
                entriesAreValid = false;

            }
            boolean fastaListIsValid = ParsingRulesTester.testFastaListInsideDialog(fastaList);
            if (!fastaListIsValid) {
                Popup.warning("The list of fasta names contains some non valid regular expressions\n " +
                        "they are displayed in red inside the table"
                );

                setStatus(true, "at least one regular expression is not valid inside table");
                entriesAreValid = false;
            }

        }
        return entriesAreValid;
    }

    /**
     * retrieves the parsingrule
     *
     * @return parsingrule
     */

    public ParsingRule getParsingRuleInsideDialog() {

        return new ParsingRule(labelField.getText().trim(), fastaList, fastaVersionTField.getText().trim(), proteinAccTField.getText().trim());
    }

    // close The dialog
    @Override
    protected boolean cancelCalled() {

        return true;
    }

    /**
     * resets elements inside dialog
     */
    protected boolean backCalled() {


        labelField.setText("");
        fastaVersionTField.setText("");
        proteinAccTField.setText("");
        fastaNameTField.setText("");
        lineField.setText("");
        resultOfTest.setText("");
        fastaNamesTableModel.getDataVector().removeAllElements();
        fastaNamesTableModel.fireTableDataChanged();
        revalidate();
        repaint();

        return true;

    }

    /**
     * All methods below are used to implement the JTable
     */
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
            ArrayList<Boolean> fastaValid = ParsingRulesTester.testFastaList(fastaList);

            if (isSelected) {
                c.setBackground(Color.LIGHT_GRAY);
            } else {

                if (row % 2 == 0) {
                    c.setBackground(new Color(213, 218, 241));
                } else {

                    c.setBackground(Color.WHITE);
                }
                // reg ex not valid  appear red
                if (fastaValid.get(row).equals(false)) {
                    c.setBackground((new Color(220, 0, 90)));
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

    private void changeJTextFieldAttributes(JTextField jTextField, Color backGroundColor, Color textColor, String toolTipMessage) {
        jTextField.setBackground(backGroundColor);
        if (!jTextField.isEnabled()) {
            jTextField.setDisabledTextColor(textColor);
        } else {
            jTextField.setForeground(textColor);
        }
        jTextField.setToolTipText(toolTipMessage);
    }

}
