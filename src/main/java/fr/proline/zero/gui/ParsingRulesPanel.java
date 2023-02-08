package fr.proline.zero.gui;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.*;

import fr.proline.zero.util.*;
import fr.proline.studio.utils.IconManager;


public class ParsingRulesPanel extends JPanel {
    private JTextField labelField;

    private JTextField fastaNameTField;
    private JTextField proteinAccTField;
    private JTextField fastaVersionTField;

    private JButton buttonJTable;

    private DefaultTableModel fastaNamesTableModel;
    private Object[][] data;
    private static final String[] columns = {"Rule", "delete"};
    private static final Color jTableColor = new Color(130, 140, 190);

    private ArrayList<String> fastaList;

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
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new java.awt.Insets(9, 5, 5, 5);
        // creation des elements

        c.gridx = 0;
        c.gridy = 0;


        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        addParsingRules.add(ParsingRuleAdder(), c);
        c.gridx++;


        c.fill = GridBagConstraints.HORIZONTAL;

        c.anchor = GridBagConstraints.WEST;
        c.insets = new java.awt.Insets(3, 5, 5, 5);
        addParsingRules.add(fastaDirectoriesAdder(), c);


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
            // Popup.info("Modification canceled");

        } else {
            Popup.warning("Error while restoring parsing rule ");
        }

    }


    private JPanel createButtonPanel() {
        JPanel displayButtons = new JPanel(new GridBagLayout());
        GridBagConstraints buttonsConstraints = new GridBagConstraints();
        buttonsConstraints.insets = new Insets(3, 3, 3, 3);
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
            testActionV2();
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

    private void testActionV2() {
        System.out.println("button test pressed");

    }


    private JPanel ParsingRuleAdder() {
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

    private JPanel fastaDirectoriesAdder() {
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

        fastaNamesTableModel = new DefaultTableModel(data, columns);
        fastaNamesTable = new JTable();
        // button represents the delete button in the jtable
        buttonJTable = new JButton();
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

        // Debug
        System.out.println("hauteur: " + fastaPanel.getHeight());

        return fastaPanel;


    }

    private JPanel createProteinAccDefaultRule() {
        JPanel displayProt = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        displayProt.setBorder(BorderFactory.createTitledBorder(""));
        String protByDefault = JsonSeqRepoAccess.getInstance().getDefaultProtein();
        constraints.insets = new Insets(5, 5, 5, 40);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        displayProt.add(new JLabel("Default Protein Accession Rule: "), constraints);

        JTextField labelProt = new JTextField(protByDefault);
        labelProt.setForeground(Color.blue);
        constraints.gridx++;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        labelProt.setPreferredSize(new Dimension(120,20));
        constraints.insets=new Insets(5,45,5,5);
        displayProt.add(labelProt, constraints);
        constraints.gridx++;
        displayProt.add(new JLabel("           "), constraints);
        return displayProt;
    }

    // Concatenate Strings from list of Fasta names
    private String buildFastas(ArrayList<String> fastaNames) {
        String builtFasta = "";
        for (int k = 0; k < fastaNames.size(); k++) {
            if (k != fastaNames.size() - 1) {
                builtFasta = builtFasta + fastaNames.get(k) + "; ";
            } else {
                builtFasta = builtFasta + fastaNames.get(k);
            }
        }
        return builtFasta;

    }

    private JPanel createParsingRulesListPanel() {
        JPanel displayRules = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        displayRules.setBorder(BorderFactory.createTitledBorder("List of parsing rules"));
        ArrayList<ParsingRule> setOfRules = ConfigManager.getInstance().getParsingRulesManager().getSetOfRules();
        constraints.gridy = 0;
        constraints.gridx = 0;
        // maximumSizeOfText contains maximum sizes in pixels of the 4 JTextFields
        // used to improve alignements
        ArrayList<Integer> maximumSizeOfText = getMaximum();


        for (int i = 0; i < setOfRules.size(); i++) {
            constraints.gridx = 0;

            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(5, 1, 5, 1);
            constraints.weightx = 1;
            displayRules.add(displayParsingRules(setOfRules.get(i), maximumSizeOfText), constraints);

            JButton editButton = new JButton(IconManager.getIcon(IconManager.IconType.EDIT));
            editButton.setHorizontalAlignment(SwingConstants.LEFT);
            editButton.setToolTipText("Click to edit the parsing rule");
            final int finalI = i;

            editButton.addActionListener(e -> {
                editRule(setOfRules.get(finalI));
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
                deleteRule(setOfRules.get(finalI));
            });
            constraints.anchor = GridBagConstraints.SOUTHWEST;
            constraints.fill = GridBagConstraints.NONE;
            displayRules.add(deleteButton, constraints);
            constraints.gridy++;

        }

        return displayRules;
    }

    // Calculates maximmum sizes of String inside all parsingRules
    private ArrayList<Integer> getMaximum() {
        ArrayList<ParsingRule> setOfRules = ConfigManager.getInstance().getParsingRulesManager().getSetOfRules();
        ArrayList<Integer> maximums = new ArrayList<>(4);
        int max1 = 0;
        int max2 = 0;
        int max3 = 0;
        int max4 = 0;
        for (int i = 0; i < setOfRules.size(); i++) {
            ArrayList<Integer> sizesInPixels = getSizesOf4JtextFields(setOfRules.get(i));
            // TODO later :calculate size in pixels
            max1 = Math.max(max1, sizesInPixels.get(0));
            max2 = Math.max(max2, sizesInPixels.get(1));
            max3 = Math.max(max3, sizesInPixels.get(2));
            max4 = Math.max(max4, sizesInPixels.get(3));


        }
        maximums.add(0, max1);
        maximums.add(1, max2);
        maximums.add(2, max3);
        maximums.add(3, max4);

        return maximums;
    }

    private ArrayList<String> patchesfortests() {
        ArrayList<String> testy = new ArrayList<>();
        testy.add(0, "");
        testy.add(1, "");
        testy.add(2, "");
        testy.add(3, "");


        return testy;
    }

    private int getSizeinPixels(JTextField jTextField) {
        Font fontused = jTextField.getFont();
        FontMetrics fontMetrics = jTextField.getFontMetrics(fontused);
        String stringInTextField = jTextField.getText();

        return fontMetrics.stringWidth(stringInTextField);

    }

    private ArrayList<Integer> getSizesOf4JtextFields(ParsingRule parsingRule) {
        ArrayList<Integer> patches = new ArrayList<>(4);
        JTextField labelField = new JTextField(parsingRule.getName());
        int size1 = getSizeinPixels(labelField);
        patches.add(0, size1);
        JTextField fastaField = new JTextField(buildFastas(parsingRule.getFastaNameRegExp()));
        int size2 = getSizeinPixels(fastaField);
        patches.add(1, size2);
        JTextField fastaVersionField = new JTextField(parsingRule.getFastaVersionRegExp());
        int size3 = getSizeinPixels(fastaVersionField);
        patches.add(2, size3);
        JTextField proteinField = new JTextField(parsingRule.getProteinAccRegExp());
        int size4 = getSizeinPixels(proteinField);
        patches.add(3, size4);
        return patches;

    }


    private JPanel displayParsingRules(ParsingRule parsingRule, ArrayList<Integer> maximumSize) {
        JPanel displayPr = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        displayPr.setBorder(BorderFactory.createLineBorder(new Color(150, 160, 210), 1));


        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.gridy = 0;

        constraints.gridx = 0;
        String label = parsingRule.getName();
        String fastaVersion = parsingRule.getFastaVersionRegExp();
        String protein = parsingRule.getProteinAccRegExp();
        ArrayList<String> fastaNames = parsingRule.getFastaNameRegExp();
        JLabel jLabelname = new JLabel("Label: ");
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        displayPr.add(jLabelname, constraints);
        JTextField labelField = new JTextField(label);
        labelField.setEnabled(parsingRule.isEditable());
        labelField.setPreferredSize(new Dimension(maximumSize.get(0), 20));
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
        JTextField fastaField = new JTextField(buildFastas(fastaNames));
        fastaField.setPreferredSize(new Dimension(maximumSize.get(1), 20));
        fastaField.setEnabled(parsingRule.isEditable());
        fastaField.setEditable(parsingRule.isEditable());
        constraints.gridx++;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        displayPr.add(fastaField, constraints);
        JLabel jLabelFastaVersion = new JLabel("Fasta Version Rule: ");
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        displayPr.add(jLabelFastaVersion, constraints);
        JTextField FastaVersionRegExp = new JTextField(fastaVersion);
        FastaVersionRegExp.setEnabled(parsingRule.isEditable());
        FastaVersionRegExp.setEditable(parsingRule.isEditable());
        constraints.gridx++;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        FastaVersionRegExp.setPreferredSize(new Dimension(maximumSize.get(2), 20));
        displayPr.add(FastaVersionRegExp, constraints);
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
        proteinField.setPreferredSize(new Dimension(maximumSize.get(3), 20));
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
            ArrayList<String> list = fastaList;

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
            Object[] vector = {fastaToBeAdded, buttonJTable};
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
        ArrayList<String> fastaNames = parsingRule.getFastaNameRegExp();
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
            Object[] vector = {fastaNames.get(k), buttonJTable};
            fastaNamesTableModel.addRow(vector);
        }

    }

    //// Methods used by the jTable
    public class TableButtonRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            buttonJTable.setIcon(IconManager.getIcon(IconManager.IconType.TRASH));
            buttonJTable.setBackground(new Color(210, 210, 230));
            // button.setBackground(CyclicColorPalette.GROUP4_PALETTE[11]);
            buttonJTable.setToolTipText("Click to remove Rule");
            buttonJTable.setSize(20, 20);
            buttonJTable.setBorderPainted(false);
            Insets insets = buttonJTable.getInsets();


            return buttonJTable;

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
            return new String(label);
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
            // setBackground(new Color(170, 170, 170));
            setBorder(BorderFactory.createEtchedBorder());
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }

    }


}





