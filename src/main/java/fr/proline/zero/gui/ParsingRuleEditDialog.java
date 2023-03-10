package fr.proline.zero.gui;

import fr.proline.studio.gui.DefaultDialog;
import fr.proline.studio.utils.IconManager;
import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.ParsingRule;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ParsingRuleEditDialog extends DefaultDialog {
    private JTextField labelField;
    private JTextField fastaVersionTField;
    private JTextField proteinAccTField;
    private JTextField fastaNameTField;
    private List<String> fastaList;
    private JButton removeFastaNameRuleJButton;

    private DefaultTableModel fastaNamesTableModel;

    private JTable fastaNamesTable;
    private static String deleteColummnIdentifier = "      ";
    private static final String[] columns = {"Rule", deleteColummnIdentifier};
    private static final Color jTableColor = new Color(174, 182, 222);

    private static final Color testTableColor = new Color(200, 200, 200);

    private TypeOfDialog typeOfDialog;

    enum TypeOfDialog {Add, Edit}


    public ParsingRuleEditDialog(Window Parent, TypeOfDialog typeOfDialog, ParsingRule parsingRule) {

        super(Parent);

        //Configure commons buttons for all TypeOfDialog
        this.setButtonVisible(BUTTON_HELP, false);

        this.setButtonName(BUTTON_DEFAULT, "Test");
        this.setButtonVisible(BUTTON_DEFAULT, true);
        this.setButtonEnabled(BUTTON_DEFAULT, false);
        this.setButtonIcon(BUTTON_DEFAULT, IconManager.getIcon(IconManager.IconType.TEST));

        this.setButtonVisible(BUTTON_BACK, true);
        this.setButtonName(BUTTON_BACK, "Clear");
        this.setButtonIcon(BUTTON_BACK, IconManager.getIcon(IconManager.IconType.ERASER));

        this.setButtonIcon(BUTTON_OK, IconManager.getIcon(IconManager.IconType.PLUS_16X16));
        this.typeOfDialog = typeOfDialog;

        if (typeOfDialog.equals(TypeOfDialog.Add)) {
            this.setButtonName(BUTTON_OK, "Add");
            //this.setButtonName(BUTTON_SAVE, "Edit"); VDS Not used ?

            this.setIconImage(IconManager.getImage(IconManager.IconType.PLUS_16X16));
            this.setTitle("Add Parsing Rule");

        }
        if (typeOfDialog.equals(TypeOfDialog.Edit)) {
            this.setIconImage(IconManager.getImage(IconManager.IconType.EDIT));

            // this.setButtonName(4, "Edit"); VDS not used
            this.setButtonName(BUTTON_OK, "Update");
            this.setTitle("Edit Parsing Rule");

        }

        JPanel internalPanel = createParsingRulesJPanel(parsingRule);

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
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        addParsingRules.add(newParsingRulePanel(), c);

        c.gridx++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new java.awt.Insets(3, 5, 5, 5);
        addParsingRules.add(newFastaNamePanel(), c);


        addParsingRules.add(Box.createHorizontalGlue(), c);
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


        }
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
        fastaNamesTable.setIntercellSpacing(new Dimension(2, 2));
        fastaNamesTable.setDefaultRenderer(Object.class, new CustomRenderer());

//        fastaNamesTable.getTableHeader().setDefaultRenderer(new SimpleHeaderRenderer());


        fastaNamesTable.getColumn(deleteColummnIdentifier).setCellRenderer(new TableButtonRenderer());
        fastaNamesTable.getColumn(deleteColummnIdentifier).setCellEditor(new TableButtonEditor(new JCheckBox()));
        fastaNamesTable.getColumn(deleteColummnIdentifier).setMaxWidth(40);
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


    @Override
    protected boolean okCalled() {
        System.out.println("Ok pressed");
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
                setStatus(true, "please add a protein regex");
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
            boolean  deleteOrAdd = Popup.optionYesNO("Do you want to add the value or delete it?", options);
            if (deleteOrAdd){
                fastaNameTField.setText("");

            }
            else {
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

        return Popup.yesNo("Do you want to close this window ?");
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
            Popup.warning("please enter value");
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
                // Change color?
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
