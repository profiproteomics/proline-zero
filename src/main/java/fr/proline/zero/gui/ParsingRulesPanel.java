package fr.proline.zero.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.*;

import fr.proline.zero.util.*;
import fr.proline.studio.utils.IconManager;


public class ParsingRulesPanel extends JPanel {
    private JTextField labelField;

    private JTextField fastaNameTField;
    private JTextField proteinAccTField;
    private JTextField fastaVersionTField;

    private JButton removeFastaNameRuleJButton;

    private DefaultTableModel fastaNamesTableModel;
    //private Object[][] data;
    private static final String[] columns = {"Rule", "delete"};
    private static final Color jTableColor = new Color(130, 140, 190);

    private List<String> fastaList;

    private boolean editingContext;

    private JTable fastaNamesTable;

    private ParsingRule parsingRuleEditedBackUp;


    public ParsingRulesPanel() {
        initialize();
    }

    private void initialize() {
        // creation du layout
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1;
        c.weighty = 1;

        // creation des widgets

        HelpHeaderPanel help = new HelpHeaderPanel("Parsing Rules", SettingsConstant.PARSING_RULES_HELP_PANE);

        // ajout des widgets au layout
        c.gridx = 0;
        c.gridy = 0;
        add(help, c);

        c.insets = new java.awt.Insets(20, 15, 0, 15);
        c.gridy++;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        add(createParsingRulesPanel(), c);

        c.gridy++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        add(createProteinAccDefaultRule(), c);

        c.gridy++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        add(createParsingRulesListPanel(), c);

        c.fill = GridBagConstraints.VERTICAL;

        add(Box.createHorizontalGlue(), c);

        revalidate();
        repaint();

    }

    private void updatePanel() {
        removeAll();
        initialize();

    }

    private JPanel createParsingRulesPanel() {
        // creation du panel et du layout
        JPanel addParsingRules = new JPanel(new GridBagLayout());
        addParsingRules.setBorder(BorderFactory.createTitledBorder("Add Parsing Rule "));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new java.awt.Insets(9, 5, 5, 5);
        // creation des elements

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        addParsingRules.add(newParsingRulePanel(), c);

        c.gridx++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new java.awt.Insets(3, 5, 5, 5);
        addParsingRules.add(newFastaNamePanel(), c);

        c.gridy++;
        c.anchor = GridBagConstraints.EAST;
        addParsingRules.add(createButtonPanel(), c);
        addParsingRules.add(Box.createHorizontalGlue(), c);

        return addParsingRules;
    }


    private void cancelEditAction() {
        // rewrites the rule that has been suppressed at the beginning of editing
        boolean success = ConfigManager.getInstance().getParsingRulesManager().addNewRule(parsingRuleEditedBackUp);
        if (success) {

            editingContext = false;
            updatePanel();


        } else {
            Popup.warning("Error while restoring parsing rule ");
        }

    }


    private JPanel createButtonPanel() {
        JPanel displayButtons = new JPanel(new GridBagLayout());
        GridBagConstraints buttonsConstraints = new GridBagConstraints();
        buttonsConstraints.insets = new Insets(3, 5, 3, 5);
        buttonsConstraints.gridx = 0;
        buttonsConstraints.gridy = 0;
        buttonsConstraints.fill = GridBagConstraints.HORIZONTAL;
        JLabel spacer = new JLabel("           ");
        buttonsConstraints.weightx = 1;
        buttonsConstraints.ipadx = 5;
        displayButtons.add(spacer, buttonsConstraints);

        buttonsConstraints.fill = GridBagConstraints.NONE;
        buttonsConstraints.anchor = GridBagConstraints.EAST;
        buttonsConstraints.weightx = 0;

        JButton cancelEditButton = new JButton("Cancel");
        cancelEditButton.setEnabled(editingContext);
        Icon editCancel = IconManager.getIcon(IconManager.IconType.UNDO);
        cancelEditButton.setToolTipText("Click to cancel current editing");
        cancelEditButton.setIcon(editCancel);
        cancelEditButton.addActionListener(e -> {
            cancelEditAction();
        });
        displayButtons.add(cancelEditButton, buttonsConstraints);

        buttonsConstraints.gridx++;
        buttonsConstraints.fill = GridBagConstraints.NONE;
        buttonsConstraints.anchor = GridBagConstraints.EAST;
        JButton clearButton = new JButton("Clear");
        clearButton.setIcon(IconManager.getIcon(IconManager.IconType.CLEAR_ALL));
        clearButton.setToolTipText("Click to reset all fields");
        clearButton.addActionListener(e -> {
            clearParsingRule();
        });
        displayButtons.add(clearButton, buttonsConstraints);

        buttonsConstraints.gridx++;
        buttonsConstraints.anchor = GridBagConstraints.EAST;
        JButton testButton = new JButton("Test");
        testButton.setIcon(IconManager.getIcon(IconManager.IconType.TEST));
        testButton.addActionListener(e -> {
            testAction();
        });
        displayButtons.add(testButton, buttonsConstraints);

        buttonsConstraints.gridx++;
        buttonsConstraints.anchor = GridBagConstraints.EAST;
        JButton plus = new JButton("Add");
        plus.setIcon(IconManager.getIcon(IconManager.IconType.PLUS_16X16));
        plus.setToolTipText("Click to add your parsing rule");
        plus.addActionListener(e -> {
            addRule();
        });
        displayButtons.add(plus, buttonsConstraints);

        return displayButtons;

    }


    // TODO method called for tests

    private void testAction() {
        System.out.println("button test pressed");

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
        JLabel protlabel = new JLabel("Protein accession rule: ");
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
        parsingConstraints.fill = GridBagConstraints.HORIZONTAL;

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
        fastaNamesTable.setIntercellSpacing(new Dimension(3, 3));

        fastaNamesTable.getTableHeader().setDefaultRenderer(new SimpleHeaderRenderer());


        fastaNamesTable.getColumn("delete").setCellRenderer(new TableButtonRenderer());
        fastaNamesTable.getColumn("delete").setCellEditor(new TableButtonEditor(new JCheckBox()));
        fastaNamesTable.getColumn("delete").setMaxWidth(40);
        JScrollPane scrollPane = new JScrollPane(fastaNamesTable);

        scrollPane.setPreferredSize(new Dimension(new Dimension(110, 100)));

        parsingConstraints.gridy++;
        parsingConstraints.gridwidth = 2;
        parsingConstraints.gridx = 0;
        parsingConstraints.weightx = 1;
        parsingConstraints.fill = GridBagConstraints.HORIZONTAL;
        fastaPanel.add(scrollPane, parsingConstraints);


        return fastaPanel;


    }

    private JPanel createProteinAccDefaultRule() {
        JPanel displayProt = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        displayProt.setBorder(BorderFactory.createTitledBorder(""));
        constraints.insets = new Insets(5, 5, 5, 40);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        displayProt.add(new JLabel("Default Protein Accession Rule: "), constraints);

        String protByDefault = ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule();
        JTextField labelProt = new JTextField(protByDefault);
        labelProt.setForeground(Color.blue);
        constraints.gridx++;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        labelProt.setPreferredSize(new Dimension(120, 20));
        constraints.insets = new Insets(5, 45, 5, 5);
        displayProt.add(labelProt, constraints);
        constraints.gridx++;
        displayProt.add(new JLabel("           "), constraints);
        return displayProt;
    }


    private String fastaConcatenator(List<String> fastaNames) {
        StringBuilder builtFasta = new StringBuilder();
        for (int k = 0; k < fastaNames.size(); k++) {
            builtFasta.append(fastaNames.get(k));
            if(k < (fastaNames.size()-1))
                builtFasta.append("; ");
        }
        return builtFasta.toString();
    }

    private JPanel createParsingRulesListPanel() {
        JPanel displayRules = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        displayRules.setBorder(BorderFactory.createTitledBorder("List of parsing rules"));
        List<ParsingRule> setOfRules = ConfigManager.getInstance().getParsingRulesManager().getSetOfRules();
        constraints.gridy = 0;
        constraints.gridx = 0;

        // used to improve alignements
        int[] maximas = getMaximums(setOfRules);

        for (int i = 0; i < setOfRules.size(); i++) {
            ParsingRule currentParsingRule = setOfRules.get(i);
            constraints.gridx = 0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(5, 1, 5, 1);
            constraints.weightx = 1;
            displayRules.add(displayParsingRules(currentParsingRule, maximas), constraints);

            JButton editButton = new JButton(IconManager.getIcon(IconManager.IconType.EDIT));
            editButton.setHorizontalAlignment(SwingConstants.LEFT);
            editButton.setToolTipText("Click to edit the parsing rule");
            editButton.addActionListener(e -> {
                editRule(currentParsingRule);
            });
            constraints.gridx++;
            constraints.weightx = 0;
            constraints.fill = GridBagConstraints.NONE;
            constraints.anchor = GridBagConstraints.NORTHWEST;
            constraints.insets = new Insets(8, 1, 7, 1);
            displayRules.add(editButton, constraints);

            JButton deleteButton = new JButton(IconManager.getIcon(IconManager.IconType.TRASH));
            deleteButton.setHorizontalAlignment(SwingConstants.LEFT);
            deleteButton.setToolTipText("Click to delete the parsing rule");
            deleteButton.addActionListener(e -> {
                deleteRule(currentParsingRule);
            });
            constraints.anchor = GridBagConstraints.SOUTHWEST;
            constraints.fill = GridBagConstraints.NONE;
            displayRules.add(deleteButton, constraints);
            constraints.gridy++;

        }

        return displayRules;
    }


    private int[] getMaximums(List<ParsingRule> setOfRules) {
        int[] arrayOfMax = {0, 0, 0, 0};
        for (ParsingRule prule : setOfRules) {
            List<Integer> sizesInPixels = getSizesGeneric(prule);
            for (int i = 0; i < sizesInPixels.size(); i++) {
                arrayOfMax[i] = Math.max(arrayOfMax[i], sizesInPixels.get(i));
            }
        }
        return arrayOfMax;
    }


    private int getSizeinPixels(JTextField jTextField) {
        Font fontused = jTextField.getFont();
        FontMetrics fontMetrics = jTextField.getFontMetrics(fontused);
        String stringInTextField = jTextField.getText();
        return fontMetrics.stringWidth(stringInTextField);

    }

    private List<Integer> getSizesGeneric(ParsingRule parsingRule) {
        List<Integer> sizes = new ArrayList<>(4);
        JTextField nameField = new JTextField(parsingRule.getName());
        JTextField fastaNameField = new JTextField(fastaConcatenator(parsingRule.getFastaNameRegExp()));
        JTextField fastaVersionField = new JTextField(parsingRule.getFastaVersionRegExp());
        JTextField proteinAccField = new JTextField(parsingRule.getProteinAccRegExp());

        sizes.add(getSizeinPixels(nameField));
        sizes.add(getSizeinPixels(fastaNameField));
        sizes.add(getSizeinPixels(fastaVersionField));
        sizes.add(getSizeinPixels(proteinAccField));

        return sizes;
    }


    private JSpinner spinnerFasta(ParsingRule parsingRule) {
        List<String> fastasRules = parsingRule.getFastaNameRegExp();
        String[] str = new String[fastasRules.size()];

        for (int i = 0; i < fastasRules.size(); i++) {
            str[i] = fastasRules.get(i);
        }

        SpinnerListModel model = new SpinnerListModel(str);

        return new JSpinner(model);

    }


    private JPanel displayParsingRules(ParsingRule parsingRule, int[] maximumSize) {
        JPanel displayPr = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        displayPr.setBorder(BorderFactory.createLineBorder(new Color(150, 160, 210), 1));
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.gridy = 0;
        constraints.gridx = 0;

        String label = parsingRule.getName();
        String fastaVersion = parsingRule.getFastaVersionRegExp();
        String protein = parsingRule.getProteinAccRegExp();
        List<String> fastaNames = parsingRule.getFastaNameRegExp();
        boolean parsingRuleHasManyFastasRules = (fastaNames.size() > 1);

        JLabel jLabelname = new JLabel("Label: ");
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        displayPr.add(jLabelname, constraints);

        JTextField labelField = new JTextField(label);
        labelField.setEnabled(parsingRule.isEditable());
        labelField.setPreferredSize(new Dimension(maximumSize[0], 20));
        constraints.gridx++;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        displayPr.add(labelField, constraints);

        JLabel fastaName = new JLabel("Fasta Name Rule: ");
        constraints.gridx++;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        displayPr.add(fastaName, constraints);
        if (!parsingRuleHasManyFastasRules) {
            JTextField fastaField = new JTextField(fastaConcatenator(fastaNames));
            fastaField.setEnabled(parsingRule.isEditable());
            fastaField.setEditable(parsingRule.isEditable());
            fastaField.setPreferredSize(new Dimension(maximumSize[1], 20));
            constraints.gridx++;
            constraints.anchor = GridBagConstraints.EAST;
            constraints.weightx = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            displayPr.add(fastaField, constraints);
        } else {
            JSpinner fastaField = spinnerFasta(parsingRule);
            fastaField.setPreferredSize(new Dimension(maximumSize[1], 20));
            constraints.gridx++;
            constraints.anchor = GridBagConstraints.EAST;
            constraints.weightx = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            displayPr.add(fastaField, constraints);
        }

        JLabel jLabelFastaVersion = new JLabel("Fasta Version Rule: ");
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        displayPr.add(jLabelFastaVersion, constraints);

        JTextField fastaVersionRegExpTF = new JTextField(fastaVersion);
        fastaVersionRegExpTF.setEnabled(parsingRule.isEditable());
        fastaVersionRegExpTF.setEditable(parsingRule.isEditable());
        constraints.gridx++;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        fastaVersionRegExpTF.setPreferredSize(new Dimension(maximumSize[2], 20));
        displayPr.add(fastaVersionRegExpTF, constraints);

        JLabel jLabelProtein = new JLabel("Protein Accession Rule: ");
        constraints.gridx++;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        displayPr.add(jLabelProtein, constraints);

        JTextField proteinField = new JTextField(protein);
        proteinField.setEnabled(parsingRule.isEditable());
        proteinField.setEditable(parsingRule.isEditable());
        constraints.gridx++;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        proteinField.setPreferredSize(new Dimension(maximumSize[3], 20));
        displayPr.add(proteinField, constraints);

        return displayPr;

    }

    public void updateValues() {
        // TODO Auto-generated method stub
        updatePanel();
    }


    private void addRule() {
        boolean formFullyFilled = !labelField.getText().isEmpty() && !proteinAccTField.getText().isEmpty()
                && !fastaVersionTField.getText().isEmpty() && !fastaList.isEmpty();

        if (formFullyFilled) {
            String addedLabel = labelField.getText();
            String addedRegex = proteinAccTField.getText();
            String addedFasta = fastaVersionTField.getText();
            List<String> list = fastaList;

            ParsingRule pr = new ParsingRule(addedLabel, list, addedFasta, addedRegex);
            boolean success = ConfigManager.getInstance().getParsingRulesManager().addNewRule(pr);
            if (success) {
                editingContext = false;
                updatePanel();

            } else if (formFullyFilled && !ConfigManager.getInstance().getParsingRulesManager().isLabelExists()) {

                Popup.warning("Parsing Rule has not been added");
            }
        } else {

            Popup.warning("Missing values please fill all the fields");
        }

    }


    private void deleteRule(ParsingRule parsingRuleToBeDeleted) {
        boolean success = ConfigManager.getInstance().getParsingRulesManager().deleteRule(parsingRuleToBeDeleted);
        if (success) {
            updatePanel();
        }

    }


    private void clearParsingRule() {
        fastaVersionTField.setText("");
        fastaNameTField.setText("");
        proteinAccTField.setText("");
        labelField.setText("");

        for (int k = fastaList.size() - 1; k >= 0; k--) {
            fastaNamesTableModel.removeRow(k);

        }
        fastaList.clear();
        revalidate();
        repaint();


    }


    private void addFastaNames() {
        String fastaToBeAdded = fastaNameTField.getText();
        if (fastaToBeAdded.length() != 0) {
            fastaNameTField.setText("");
            fastaList.add(fastaToBeAdded);
            Object[] vector = {fastaToBeAdded, removeFastaNameRuleJButton};
            fastaNamesTableModel.addRow(vector);
            revalidate();
            repaint();
        } else {
            Popup.warning("please enter value");
        }


    }


    private void editRule(ParsingRule parsingRule) {
        Popup.info("You are entering edit mode \n you can cancel modifications at any time\n by clicking on the cancel button");
        editingContext = true;
        // parsingRule.setEditable(true); // to be removed
        String label = parsingRule.getName();
        String fastaVersionRegexp = parsingRule.getFastaVersionRegExp();
        String proteinRegexp = parsingRule.getProteinAccRegExp();
        List<String> fastaNames = parsingRule.getFastaNameRegExp();
        // save the parsing rule before editing, so it can be restored
        parsingRuleEditedBackUp = new ParsingRule(label, fastaNames, fastaVersionRegexp, proteinRegexp);
        // parsing rule is deleted
        boolean success = ConfigManager.getInstance().getParsingRulesManager().deleteRule(parsingRule);
        if (success) {
            updatePanel();
        }
        //display values in the adder panel so user can modify them
        fastaList = parsingRule.getFastaNameRegExp();
        labelField.setText(parsingRule.getName());
        fastaVersionTField.setText(parsingRule.getFastaVersionRegExp());
        proteinAccTField.setText(parsingRule.getProteinAccRegExp());
        // draws the jTable to be modified
        for (int k = 0; k < fastaNames.size(); k++) {
            Object[] vector = {fastaNames.get(k), removeFastaNameRuleJButton};
            fastaNamesTableModel.addRow(vector);
        }

    }

    //// Methods used by the jTable
    public class TableButtonRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            removeFastaNameRuleJButton.setIcon(IconManager.getIcon(IconManager.IconType.TRASH));
            removeFastaNameRuleJButton.setBackground(new Color(210, 210, 230));
            // button.setBackground(CyclicColorPalette.GROUP4_PALETTE[11]);
            removeFastaNameRuleJButton.setToolTipText("Click to remove Rule");
            removeFastaNameRuleJButton.setSize(20, 20);
            removeFastaNameRuleJButton.setBorderPainted(false);

            return removeFastaNameRuleJButton;

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
            setBackground(new Color(130, 140, 190));
            setBorder(BorderFactory.createEtchedBorder());
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }

    }


}





