package fr.proline.zero.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.*;

import fr.proline.zero.util.*;

public class ParsingRulesPanel extends JPanel {
    private JTextField labelField;

    private JTextField fastaInput;
    private JTextField proteinField;
    private JTextField fastaVersionField;

    private JButton button;

    private DefaultTableModel defaultTableModel;
    private Object[][] data;
    private static final String[] columns = {"Rule", "delete"};
    private static final Color jTableColor = new Color(130, 140, 190);

    private ArrayList<String> fastaList;

    private boolean editingContext;

    private JTable jTable;

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

        add(displayProteinBydefault(), c);

        c.gridy++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        add(displaySetOfRules(), c);

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
        c.insets = new java.awt.Insets(5, 5, 5, 5);
        // creation des elements
        JButton plus = new JButton("");
        JButton clearButton = new JButton("");
        JButton testButton = new JButton("Test");
        JButton cancelEditButton = new JButton("Cancel");

        try {
            Icon plusIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("plus.png")));
            plus.setIcon(plusIcon);
            plus.setToolTipText("Click to add your parsing rule");
            plus.addActionListener(addRule());
            Icon eraserIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("eraser.png")));
            clearButton.setIcon(eraserIcon);
            clearButton.addActionListener(clearParsingRule());
            Icon testIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("tick.png")));
            testButton.setIcon(testIcon);
            testButton.setEnabled(false);
            testButton.addActionListener(testAction());
            cancelEditButton.setEnabled(editingContext);
            Icon editIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("arrow-circle.png")));
            cancelEditButton.setIcon(editIcon);
            cancelEditButton.addActionListener(cancelEditAction());
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        c.gridx = 0;
        c.gridy = 0;


        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        addParsingRules.add(ParsingRuleAdder(), c);
        c.gridx++;


        c.fill = GridBagConstraints.HORIZONTAL;

        c.anchor = GridBagConstraints.WEST;

        addParsingRules.add(fastaDirectoriesAdder(), c);


        c.gridy++;

        c.anchor = GridBagConstraints.EAST;


        addParsingRules.add(displayButtons(clearButton, testButton, plus, cancelEditButton), c);
        addParsingRules.add(Box.createHorizontalGlue(), c);

        return addParsingRules;
    }

    private ActionListener cancelEditAction() {
        ActionListener editAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // rewrites the rule that has been suppressed at the beginning of editing
                boolean success = ConfigManager.getInstance().getParsingRulesManager().addNewRule(parsingRuleEditedBackUp);
                if (success) {
                    editingContext = false;
                    updatePanel();

                } else {
                    Popup.warning("Error during backup ");
                }
            }
        };
        return editAction;

    }


    private JPanel displayButtons(JButton clearButton, JButton testButton, JButton plus, JButton editButton) {
        JPanel displayButtons = new JPanel(new GridBagLayout());
        GridBagConstraints buttonsConstraints = new GridBagConstraints();
        buttonsConstraints.gridx = 0;
        buttonsConstraints.gridy = 0;
        buttonsConstraints.fill = GridBagConstraints.HORIZONTAL;
        JLabel spacer = new JLabel("           ");
        buttonsConstraints.weightx = 1;
        buttonsConstraints.ipadx = 5;
        displayButtons.add(spacer, buttonsConstraints);
        /*add(Box.createHorizontalStrut(100),buttonsConstraints);*/
        buttonsConstraints.fill = GridBagConstraints.NONE;
        buttonsConstraints.anchor = GridBagConstraints.EAST;
        buttonsConstraints.weightx = 0;
        displayButtons.add(editButton, buttonsConstraints);
        buttonsConstraints.gridx++;
        buttonsConstraints.fill = GridBagConstraints.NONE;
        buttonsConstraints.anchor = GridBagConstraints.EAST;
        displayButtons.add(clearButton, buttonsConstraints);
        buttonsConstraints.gridx++;
        buttonsConstraints.anchor = GridBagConstraints.EAST;
        displayButtons.add(testButton, buttonsConstraints);
        buttonsConstraints.gridx++;
        buttonsConstraints.anchor = GridBagConstraints.EAST;
        displayButtons.add(plus, buttonsConstraints);
        return displayButtons;

    }

    // TODO method called for tests
    private ActionListener testAction() {
        ActionListener testListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("button test pressed");
            }
        };
        return testListener;

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
        fastaVersionField = new JTextField();
        constraints.gridx++;
        fastaVersionField.setPreferredSize(new Dimension(150, 20));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        parsingPanel.add(fastaVersionField, constraints);
        constraints.gridy++;
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.SOUTHEAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        JLabel protlabel = new JLabel("Protein accession rule: ");
        parsingPanel.add(protlabel, constraints);
        proteinField = new JTextField();
        constraints.gridx++;
        proteinField.setPreferredSize(new Dimension(150, 20));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        parsingPanel.add(proteinField, constraints);
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

        JButton addButton = new JButton("add");
        try {

            Icon addIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("plus.png")));
            addButton = new JButton(addIcon);
            addButton.setToolTipText("Click to add fasta name rule entered above");
            addButton.addActionListener(addfastaNames());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        fastaInput = new JTextField();

        parsingConstraints.anchor = GridBagConstraints.NORTHWEST;
        parsingConstraints.weightx = 1;
        fastaInput.setPreferredSize(new Dimension(170, 23));
        fastaPanel.add(fastaInput, parsingConstraints);
        parsingConstraints.gridx++;
        parsingConstraints.weightx = 0;
        parsingConstraints.fill = GridBagConstraints.NONE;
        parsingConstraints.anchor = GridBagConstraints.EAST;
        fastaPanel.add(addButton, parsingConstraints);
        parsingConstraints.gridx = 0;
        parsingConstraints.gridy++;
        parsingConstraints.anchor = GridBagConstraints.CENTER;
        fastaPanel.add(new JLabel("Fasta Name Rules: "), parsingConstraints);

        defaultTableModel = new DefaultTableModel(data, columns);
        jTable = new JTable();
        // button represents the delete button in the jtable
        button = new JButton();
        jTable.setModel(defaultTableModel);
        jTable.setGridColor(jTableColor);
        jTable.setRowHeight(20);
        jTable.setShowGrid(true);
        jTable.setIntercellSpacing(new Dimension(3, 3));


        jTable.getTableHeader().setDefaultRenderer(new SimpleHeaderRenderer());


        jTable.getColumn("delete").setCellRenderer(new TableButtonRenderer());
        jTable.getColumn("delete").setCellEditor(new TableButtonEditor(new JCheckBox()));
        jTable.getColumn("delete").setMaxWidth(40);
        JScrollPane scrollPane = new JScrollPane(jTable);

        scrollPane.setPreferredSize(new Dimension(new Dimension(110, 100)));
        // parsingConstraints.weightx=2;
        parsingConstraints.gridy++;
        parsingConstraints.gridwidth = 2;
        parsingConstraints.gridx = 0;
        parsingConstraints.weightx = 1;
        parsingConstraints.fill = GridBagConstraints.HORIZONTAL;
        fastaPanel.add(scrollPane, parsingConstraints);

        parsingConstraints.gridx--;

        // Debug
        System.out.println("hauteur: " + fastaPanel.getHeight());

        return fastaPanel;


    }

    private JPanel displayProteinBydefault() {
        JPanel displayProt = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        displayProt.setBorder(BorderFactory.createTitledBorder(""));
        String protByDefault = JsonSeqRepoAccess.getInstance().getDefaultProtein();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        displayProt.add(new JLabel("Default Protein Accession Rule"), constraints);
        JTextField labelProt = new JTextField(protByDefault);
        labelProt.setForeground(Color.blue);
        constraints.gridx++;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
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

    private JPanel displaySetOfRules() {
        JPanel displayRules = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        displayRules.setBorder(BorderFactory.createTitledBorder("List of parsing rules"));
        ArrayList<ParsingRule> setOfRules = ConfigManager.getInstance().getParsingRulesManager().getSetOfRules();
        constraints.gridy = 0;
        constraints.gridx = 0;
        Icon deleteIcon;
        Icon editIcon;
        try {
            deleteIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("cross.png")));
            editIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("tick.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < setOfRules.size(); i++) {
            constraints.gridx = 0;

            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(5, 1, 5, 1);
            constraints.weightx = 1;
            displayRules.add(displayParsingRules(setOfRules.get(i)), constraints);
            // edit button
            JButton editButton = new JButton("Edit");
            editButton.setHorizontalAlignment(SwingConstants.LEFT);
            editButton.setToolTipText("Click to edit the parsing rule");
            editButton.addActionListener(editRule(setOfRules.get(i)));
            constraints.gridx++;
            constraints.weightx = 0;
            constraints.fill = GridBagConstraints.NONE;
            constraints.anchor = GridBagConstraints.NORTHWEST;
            constraints.insets = new Insets(8, 1, 7, 1);
            displayRules.add(editButton, constraints);
            JButton deleteButton = new JButton(deleteIcon);
            deleteButton.setHorizontalAlignment(SwingConstants.LEFT);
            deleteButton.setToolTipText("Click to delete the parsing rule");
            deleteButton.addActionListener(deleteRule(setOfRules.get(i)));
            constraints.anchor = GridBagConstraints.SOUTHWEST;
            constraints.fill = GridBagConstraints.NONE;
            displayRules.add(deleteButton, constraints);
            constraints.gridy++;

        }

        return displayRules;
    }


    private JPanel displayParsingRules(ParsingRule parsingRule) {
        JPanel displayPr = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        displayPr.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
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
        labelField.setEditable(parsingRule.isEditable());
        labelField.setEditable(parsingRule.isEditable());

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
        displayPr.add(proteinField, constraints);

        return displayPr;

    }

    public void updateValues() {
        // TODO Auto-generated method stub
        updatePanel();
    }

    private ActionListener addRule() {
        ActionListener addParseRule = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean formFullyFilled = !labelField.getText().isEmpty() && !proteinField.getText().isEmpty()
                        && !fastaVersionField.getText().isEmpty() && !fastaList.isEmpty();

                if (formFullyFilled) {
                    String addedLabel = labelField.getText();
                    String addedRegex = proteinField.getText();
                    String addedFasta = fastaVersionField.getText();
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
        };
        return addParseRule;
    }

    private ActionListener deleteRule(ParsingRule parsingRuleToBeDeleted) {
        ActionListener dellParseRule = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean success = ConfigManager.getInstance().getParsingRulesManager().deleteRule(parsingRuleToBeDeleted);
                if (success) {
                    updatePanel();
                }
            }
        };
        return dellParseRule;
    }

    private ActionListener clearParsingRule() {
        ActionListener clearFields = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                fastaVersionField.setText("");
                fastaInput.setText("");
                proteinField.setText("");
                labelField.setText("");

                for (int k = fastaList.size() - 1; k >= 0; k--) {
                    defaultTableModel.removeRow(k);

                }
                fastaList.clear();
                revalidate();
                repaint();

            }
        };
        return clearFields;
    }

    private ActionListener addfastaNames() {
        ActionListener fastaListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String fastaToBeAdded = fastaInput.getText();
                if (fastaToBeAdded.length() != 0) {
                    fastaInput.setText("");
                    fastaList.add(fastaToBeAdded);
                    Object[] vector = {fastaToBeAdded, button};
                    defaultTableModel.addRow(vector);
                    revalidate();
                    repaint();
                } else {
                    Popup.warning("please enter value");
                }
            }
        };
        return fastaListener;

    }

    private ActionListener editRule(ParsingRule parsingRule) {
        ActionListener editParseRule = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Popup.info("You are entering edit mode \n you can cancel modifications at any time\n by clicking on the cancel button");
                editingContext = true;
                parsingRule.setEditable(true); // to be removed
                String label = parsingRule.getName();
                String fastaVersionRegexp = parsingRule.getFastaVersionRegExp();
                String proteinRegexp = parsingRule.getProteinAccRegExp();
                ArrayList<String> fastaNames = parsingRule.getFastaNameRegExp();
                // save the parsing rule before editing so it can be restored
                parsingRuleEditedBackUp = new ParsingRule(label, fastaNames, fastaVersionRegexp, proteinRegexp);
                // parsing rule is deleted
                boolean success = ConfigManager.getInstance().getParsingRulesManager().deleteRule(parsingRule);
                if (success) {
                    updatePanel();
                }
                //display values in the adder panel so user can edit them
                fastaList = parsingRule.getFastaNameRegExp();
                labelField.setText(parsingRule.getName());
                fastaVersionField.setText(parsingRule.getFastaVersionRegExp());
                proteinField.setText(parsingRule.getProteinAccRegExp());
                // draws the jTable
                for (int k = 0; k < fastaNames.size(); k++) {
                    Object[] vector = {fastaNames.get(k), button};
                    defaultTableModel.addRow(vector);
                }

            }
        };
        return editParseRule;

    }

    //// Methods used by the jTable
    public class TableButtonRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(UIManager.getColor("Button.background"));
            }

            Icon eraserIcon = null;
            try {
                eraserIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("jbutonTable.png")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            button.setIcon(eraserIcon);
            button.setBackground(new Color(130, 140, 190));
            button.setSize(20, 20);
            button.setBorderPainted(false);


            return button;

        }

    }

    public class TableButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;

        private boolean isPushed;

        public TableButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getCellEditorValue();
                    fireEditingStopped();

                }
            });
        }


        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = (value == null) ? "" : value.toString();


            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int indexToBeDeleted = jTable.getSelectedRow();

                if (jTable.isEditing()) {
                    jTable.getCellEditor().stopCellEditing();
                }

                defaultTableModel.removeRow(indexToBeDeleted);
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
            setBorder(BorderFactory.createEtchedBorder());
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }

    }


}





