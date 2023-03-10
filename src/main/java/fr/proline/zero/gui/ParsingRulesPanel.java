package fr.proline.zero.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import fr.proline.studio.gui.DefaultDialog;
import fr.proline.zero.util.*;
import fr.proline.studio.utils.IconManager;


public class ParsingRulesPanel extends JPanel {

    public ParsingRulesPanel() {
        initialize();

    }


    private void initialize() {
        // creation du layout
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 2;
        c.insets = new java.awt.Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        HelpHeaderPanel help = new HelpHeaderPanel("Parsing Rules", SettingsConstant.PARSING_RULES_HELP_PANE);
        add(help, c);

        c.gridwidth = 1;
        c.insets = new java.awt.Insets(5, 5, 5, 5);
        c.gridy++;
        c.fill = GridBagConstraints.NONE;
        c.weighty = 0;
        c.weightx = 0;
        c.anchor = GridBagConstraints.EAST;
        add( new JLabel("Add Parsing Rule: "), c);

        JButton jButtonOpenJdialog = new JButton();
        jButtonOpenJdialog.setIcon(IconManager.getIcon(IconManager.IconType.PLUS_16X16));
        jButtonOpenJdialog.addActionListener(e -> {
            openAddDialog();
        });
        c.gridx++;
        c.anchor = GridBagConstraints.WEST;
        add(jButtonOpenJdialog, c);

        c.gridx = 0;
        c.gridy++;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.weighty = 0;
        c.anchor = GridBagConstraints.EAST;
        add(new JLabel("Default Protein Accession Rule: "), c);

        String protByDefault = ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule();
        JTextField labelProt = new JTextField(protByDefault);
        c.anchor = GridBagConstraints.WEST;
        c.gridx++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        labelProt.setPreferredSize(new Dimension(200, 20));
        add(labelProt, c);

        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weighty = 0;
        add(createParsingRulesListPanel(), c);

        c.gridy++;
        c.anchor = GridBagConstraints.NORTHEAST;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        add(globalButtonTest(), c);

        c.fill = GridBagConstraints.VERTICAL;
        c.weighty = 1;
        add(Box.createVerticalGlue(), c);

        revalidate();
        repaint();


    }

    private void openAddDialog() {
        ParsingRuleEditDialog  newDialog = new ParsingRuleEditDialog(ConfigWindow.getInstance(), ParsingRuleEditDialog.TypeOfDialog.Add, null);
        newDialog.centerToWindow(ConfigWindow.getInstance());
        newDialog.setSize(630, 300);
        newDialog.centerToScreen();
        newDialog.setVisible(true);
        if (newDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {
            ParsingRule parsingRuleAdded = newDialog.getParsingRuleInsideDialog();
            boolean addSuccess = ConfigManager.getInstance().getParsingRulesManager().addNewRule(parsingRuleAdded);
            if (addSuccess) {
                updatePanel();
            } else {
                Popup.warning("Error while adding the parsing rule");
            }

        }
    }



    //VDS nom : createAddParsingRuleAccessPanel ?
//    private JPanel jdialogAccessPanel() {
//        JPanel openDialog = new JPanel(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//
//        gbc.insets = new Insets(5, 5, 5, 5);
//        gbc.gridy = 0;
//        gbc.gridx = 0;
//        gbc.anchor = GridBagConstraints.WEST;
//        gbc.weightx = 0;
//
//        JLabel infoJlabel = new JLabel("Add Parsing Rule: ");
//        openDialog.add(infoJlabel, gbc);
//
//
//        JButton jButtonOpenJdialog = new JButton();
//        jButtonOpenJdialog.setIcon(IconManager.getIcon(IconManager.IconType.PLUS_16X16));
//        jButtonOpenJdialog.addActionListener(e -> {
//
//          ParsingRuleEditDialog  newDialog = new ParsingRuleEditDialog(ConfigWindow.getInstance(), ParsingRuleEditDialog.TypeOfDialog.Add, null);
//            newDialog.centerToWindow(ConfigWindow.getInstance());
//            newDialog.setSize(630, 300);
//            newDialog.centerToScreen();
//            newDialog.setVisible(true);
//            if (newDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {
//                ParsingRule parsingRuleAdded = newDialog.getParsingRuleInsideDialog();
//                boolean addSuccess = ConfigManager.getInstance().getParsingRulesManager().addNewRule(parsingRuleAdded);
//                if (addSuccess) {
//                    updatePanel();
//                } else {
//                    Popup.warning("Error while adding the parsing rule");
//                }
//
//            }
//
//
//        });
//        gbc.gridx++;
//
//        gbc.fill = GridBagConstraints.NONE;
//        gbc.anchor = GridBagConstraints.WEST;
//        gbc.weightx = 0;
//        openDialog.add(jButtonOpenJdialog, gbc);
//        add(Box.createHorizontalGlue());
//
//        return openDialog;
//    }

    private void updatePanel() {
        removeAll();
        initialize();
    }


//    private JPanel createProteinAccDefaultRule() {
//        JPanel displayProt = new JPanel(new GridBagLayout());
//        GridBagConstraints constraints = new GridBagConstraints();
//        //displayProt.setBorder(BorderFactory.createTitledBorder(""));
//        constraints.insets = new Insets(5, 5, 5, 5);
//        constraints.gridx = 0;
//        constraints.gridy = 0;
//        constraints.anchor = GridBagConstraints.WEST;
//        constraints.fill = GridBagConstraints.NONE;
//        constraints.weightx = 0;
//        displayProt.add(new JLabel("Default Protein Accession Rule: "), constraints);
//
//        String protByDefault = ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule();
//        JTextField labelProt = new JTextField(protByDefault);
//        labelProt.setForeground(Color.DARK_GRAY);
//        constraints.gridx++;
//        constraints.anchor = GridBagConstraints.WEST;
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        constraints.weightx = 1;
//        labelProt.setPreferredSize(new Dimension(200, 20));
//        constraints.insets = new Insets(5, 5, 5, 5);
//        displayProt.add(labelProt, constraints);
//        add(Box.createHorizontalGlue());
//
//        return displayProt;
//    }


    private String fastaConcatenator(List<String> fastaNames) {
        StringBuilder builtFasta = new StringBuilder();
        for (int k = 0; k < fastaNames.size(); k++) {
            builtFasta.append(fastaNames.get(k));
            if (k < (fastaNames.size() - 1))
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
        int[] maximas = getMaximums(setOfRules);

        for (int i = 0; i < setOfRules.size(); i++) {
            ParsingRule currentParsingRule = setOfRules.get(i);
            constraints.gridx = 0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(5, 1, 5, 1);
            constraints.weightx = 1;
            displayRules.add(displayParsingRules(currentParsingRule, maximas, i), constraints);


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


    private JPanel displayParsingRules(ParsingRule parsingRule, int[] maximumSize, int index) {
        JPanel displayPr = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        displayPr.setBorder(BorderFactory.createLineBorder(new Color(150, 160, 210), 1));
        constraints.insets = new Insets(3, 5, 3, 5);
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
        // JSpinner removed
        JTextField fastaField = new JTextField(fastaConcatenator(fastaNames));
        fastaField.setEnabled(parsingRule.isEditable());
        fastaField.setEditable(parsingRule.isEditable());
        fastaField.setPreferredSize(new Dimension(maximumSize[1], 20));
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

        constraints.gridx = 3;
        constraints.gridy++;

        constraints.anchor=GridBagConstraints.EAST;
        constraints.fill=GridBagConstraints.NONE;
        constraints.insets=new Insets(0,2,3,4);
        displayPr.add(setOfButtonPanel(parsingRule),constraints);
        // add(Box.createHorizontalGlue());

        return displayPr;

    }

    public void updateValues() {
        // TODO Auto-generated method stub
        updatePanel();
        ConfigWindow.getInstance().pack();
    }
    // JPanel with two buttons for each parsing rule
    private JPanel setOfButtonPanel(ParsingRule parsingRule){
        JPanel setOfButtonPanel=new JPanel(new GridBagLayout());
        GridBagConstraints gbc=new GridBagConstraints();

        JButton editButton = new JButton(IconManager.getIcon(IconManager.IconType.EDIT));
        editButton.setHorizontalAlignment(SwingConstants.LEFT);
        editButton.setToolTipText("Click to edit the parsing rule");
        editButton.setMargin(new Insets(1, 13, 1, 13));
        editButton.addActionListener(e -> {
            //  editing = true;
            editRule(parsingRule);
        });
        JButton deleteButton = new JButton(IconManager.getIcon(IconManager.IconType.TRASH));
        deleteButton.setHorizontalAlignment(SwingConstants.LEFT);
        deleteButton.setToolTipText("Click to delete the parsing rule");
        deleteButton.setMargin(new Insets(1, 13, 1, 13));
        deleteButton.addActionListener(e -> {
            deleteRule(parsingRule);
        });
        gbc.insets=new Insets(0,2,0,0);

        gbc.gridx=0;
        gbc.gridy=0;
        gbc.fill=GridBagConstraints.NONE;
        gbc.anchor=GridBagConstraints.CENTER;
        setOfButtonPanel.add(editButton,gbc);
        gbc.gridx++;
        gbc.anchor=GridBagConstraints.EAST;
        setOfButtonPanel.add(deleteButton,gbc);

        return setOfButtonPanel;

    }

    private JButton globalButtonTest() {
        JButton testButton = new JButton("Test");
        testButton.setIcon(IconManager.getIcon(IconManager.IconType.TEST));
        testButton.setEnabled(false);
        testButton.addActionListener(e -> {
            System.out.println("global test button pressed");


        });
        testButton.setSize(30, 30);
        return testButton;
    }


    private void deleteRule(ParsingRule parsingRuleToBeDeleted) {
        boolean deleteConfirmation = Popup.yesNo("Are you sure you want to delete this parsing rule?");
        if (deleteConfirmation) {
            boolean success = ConfigManager.getInstance().getParsingRulesManager().deleteRule(parsingRuleToBeDeleted);
            if (success) {
                updatePanel();
            }
        }

    }


    private void editRule(ParsingRule parsingRule) {


        List<ParsingRule> setOfRules = ConfigManager.getInstance().getParsingRulesManager().getSetOfRules();
        int index = setOfRules.indexOf(parsingRule);


       ParsingRuleEditDialog newDialog = new ParsingRuleEditDialog(ConfigWindow.getInstance(), ParsingRuleEditDialog.TypeOfDialog.Edit, parsingRule);

        newDialog.centerToScreen();
        newDialog.setVisible(true);
        newDialog.setSize(630, 280);

        if (newDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {
            ParsingRule editedParsingRule = newDialog.getParsingRuleInsideDialog();


            parsingRule = ConfigManager.getInstance().getParsingRulesManager().updateParsingRule(parsingRule, editedParsingRule.getName(),
                    editedParsingRule.getFastaVersionRegExp(),
                    editedParsingRule.getProteinAccRegExp(), editedParsingRule.getFastaNameRegExp());
            ConfigManager.getInstance().getParsingRulesManager().updateSetOfRules(index, parsingRule);
            updatePanel();
        }

    }


}





