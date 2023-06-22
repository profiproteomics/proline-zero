package fr.proline.zero.gui;

import fr.proline.studio.gui.DefaultDialog;
import fr.proline.studio.utils.IconManager;
import fr.proline.zero.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JPanel that display Parsing rules and wich allows CRUD operations
 * a global test over all fasta files is also possible
 */

public class ParsingRulesPanel extends JPanel {

    private static final Logger LOG = LoggerFactory.getLogger(ParsingRulesPanel.class);

    private final static Color SOFT_ERROR_COLOR = new Color(145, 147, 154);

    public ParsingRulesPanel() {
        initialize();
    }


    private void initialize() {
        // creation du layout
        if (!ConfigManager.getInstance().noSeqRepoConfigFile()) {
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
            add(new JLabel("Add parsing rule: "), c);

            JButton jButtonOpenJDialog = new JButton();
            jButtonOpenJDialog.setIcon(IconManager.getIcon(IconManager.IconType.PLUS_16X16));
            jButtonOpenJDialog.setToolTipText("Click to add a parsing rule");
            jButtonOpenJDialog.addActionListener(e -> openAddDialog());
            c.gridx++;
            c.anchor = GridBagConstraints.WEST;
            add(jButtonOpenJDialog, c);

            c.gridx = 0;
            c.gridy++;
            c.fill = GridBagConstraints.NONE;
            c.gridwidth = 1;
            c.weighty = 0;
            c.anchor = GridBagConstraints.EAST;
            add(new JLabel("Default protein accession rule: "), c);


            String proteinRuleByDefault = ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule();

            if (proteinRuleByDefault.equals("")) {
                proteinRuleByDefault = ">\\w{2}\\|\\w+\\|(\\w+)";
                ConfigManager.getInstance().getParsingRulesManager().setProteinByDefault(proteinRuleByDefault);
            }

            JTextField jTextLabelProt = new JTextField(proteinRuleByDefault);
            int numColumns = Math.max(8, proteinRuleByDefault.length());
            jTextLabelProt.setColumns(numColumns);

            jTextLabelProt.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {

                    String textInsideTextField = jTextLabelProt.getText();

                    ConfigManager.getInstance().setProteinByDefault(textInsideTextField);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {

                    String textInsideTextField = jTextLabelProt.getText();

                    ConfigManager.getInstance().setProteinByDefault(textInsideTextField);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {

                }
            });
            c.anchor = GridBagConstraints.WEST;
            c.gridx++;
            // c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 0.5;
            jTextLabelProt.setPreferredSize(jTextLabelProt.getPreferredSize());
            add(jTextLabelProt, c);



            JScrollPane scrollPane = new JScrollPane(createParsingRulesListPanel());
            int numberOfRules=ConfigManager.getInstance().getParsingRulesManager().getSetOfRules().size();
            if (numberOfRules<5){
            scrollPane.setPreferredSize(new Dimension(750, numberOfRules*100));
            }
            else {scrollPane.setPreferredSize(new Dimension(750,500));}
            scrollPane.setBorder(null);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            c.weightx = 1;
            c.gridx = 0;
            c.gridwidth = 2;
            c.gridy++;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.NORTH;
            c.weighty = 0;
            add(scrollPane, c);
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


    }

    /**
     * Opens a dialog box to add a new parsing rules
     * @see DefaultDialog
     */

    private void openAddDialog() {
        ParsingRuleEditDialog adderDialog = new ParsingRuleEditDialog(ConfigWindow.getInstance(), ParsingRuleEditDialog.TypeOfDialog.Add, null);

        adderDialog.setSize(630, 380);
        adderDialog.setHelpHeader(IconManager.getIcon(IconManager.IconType.INFORMATION), "", "This dialog allows you to add new parsing rules,\n" +
                "you can also test the validity of the protein accession rule, to do so enter a line from a fasta file and click on the test button");

        adderDialog.centerToWindow(ConfigWindow.getInstance());
        adderDialog.setVisible(true);

        if (adderDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {
            ParsingRule parsingRuleAdded = adderDialog.getParsingRuleInsideDialog();
            boolean addSuccess = ConfigManager.getInstance().getParsingRulesManager().addNewRule(parsingRuleAdded);
            if (addSuccess) {
                updatePanel();
                ConfigWindow.getInstance().pack();
            } else {
                Popup.warning("Error while adding the parsing rule");
            }

        }
    }


    private void updatePanel() {
        removeAll();
        initialize();

    }


    // concatenate only two first rules
    private String fastaConcatenator(List<String> fastaNames) {
        StringBuilder builtFasta = new StringBuilder();
        for (int k = 0; k < fastaNames.size() && k < 2; k++) {
            builtFasta.append(fastaNames.get(k));
            if (k < 1 && fastaNames.size() > k + 1)
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
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.fill=GridBagConstraints.BOTH;
        constraints.insets = new Insets(5, 1, 5, 1);
        constraints.weightx = 1;
        constraints.anchor=GridBagConstraints.NORTH;

        displayRules.setPreferredSize(new Dimension(730,100*setOfRules.size()));

        int[] maximas = getMaximums(setOfRules);

        for (ParsingRule currentParsingRule : setOfRules) {
            displayRules.add(displayParsingRules(currentParsingRule, maximas), constraints);
            constraints.gridy++;
        }
        return displayRules;
    }

    /**
     *
     * @param setOfRules
     * @return the maximum sizes of the 4 Jtextfields for all parsing rules
     */
    private int[] getMaximums(List<ParsingRule> setOfRules) {
        int[] arrayOfMax = {0, 0, 0, 0};
        for (ParsingRule parsingRule : setOfRules) {
            List<Integer> sizesInPixels = getSizesGeneric(parsingRule);
            for (int i = 0; i < sizesInPixels.size(); i++) {
                arrayOfMax[i] = Math.max(arrayOfMax[i], sizesInPixels.get(i));
            }
        }
        return arrayOfMax;
    }


    private int getSizeInPixels(JTextField jTextField) {
        Font fontUsed = jTextField.getFont();
        FontMetrics fontMetrics = jTextField.getFontMetrics(fontUsed);
        String stringInTextField = jTextField.getText();
        return fontMetrics.stringWidth(stringInTextField);

    }

    private int getSizeInPixelsButton(JButton button) {
        Font fontUsed = button.getFont();
        FontMetrics fontMetrics = button.getFontMetrics(fontUsed);
        String stringInsideButton = button.getText();
        return fontMetrics.stringWidth(stringInsideButton);

    }

    /**
     *
     * @param parsingRule
     * @return the sizes in pixels of the 4 JtextFields associated to a parsing rule
     */

    private List<Integer> getSizesGeneric(ParsingRule parsingRule) {

        List<Integer> sizes = new ArrayList<>(4);
        JTextField nameField = new JTextField(parsingRule.getName());
        JTextField fastaNameField = new JTextField(fastaConcatenator(parsingRule.getFastaNameRegExp()));
        boolean manyFastaRules = parsingRule.getFastaNameRegExp().size() > 2;
        int val = (manyFastaRules) ? 1 : 0;
        JButton button = new JButton("...");
        JTextField fastaVersionField = new JTextField(parsingRule.getFastaVersionRegExp());
        JTextField proteinAccField = new JTextField(parsingRule.getProteinAccRegExp());
        sizes.add(getSizeInPixels(nameField));
        sizes.add(getSizeInPixels(fastaNameField) + getSizeInPixelsButton(button) * val);
        sizes.add(getSizeInPixels(fastaVersionField));
        sizes.add(getSizeInPixels(proteinAccField));

        return sizes;
    }


    /**
     * builds the JPanel that display one parsing rule
     *@param parsingRule
     * @param maximumSize
     */
    private JPanel displayParsingRules(ParsingRule parsingRule, int[] maximumSize) {
        JPanel displayParsingRule = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        displayParsingRule.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.gridy = 0;
        constraints.gridx = 0;

        String label = parsingRule.getName();
        String fastaVersion = parsingRule.getFastaVersionRegExp();
        String protein = parsingRule.getProteinAccRegExp();
        List<String> fastaNames = parsingRule.getFastaNameRegExp();
        boolean parsingRuleHasManyFastaRules = (fastaNames.size() > 2);
        boolean parsingRuleFastasAreNotValid=ParsingRulesTester.parsingRuleFastaRegexisNotValid(parsingRule);
        boolean proteinAccessionRuleIsValid=ParsingRulesTester.isRegexValid(parsingRule.getProteinAccRegExp());

        JLabel jLabelName = new JLabel("Label: ");
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        displayParsingRule.add(jLabelName, constraints);

        JTextField labelField = new JTextField(label);
        labelField.setEnabled(false);
        labelField.setPreferredSize(new Dimension(maximumSize[0] + 15, 20));


        constraints.gridx++;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        displayParsingRule.add(labelField, constraints);

        JLabel fastaName = new JLabel("Fasta name rule: ");
        constraints.gridx++;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        displayParsingRule.add(fastaName, constraints);

        JTextField fastaField = new JTextField(fastaConcatenator(fastaNames));
        fastaField.setEnabled(false);
        fastaField.setEditable(false);

        if (parsingRuleHasManyFastaRules) {
            fastaField.setPreferredSize(new Dimension(maximumSize[1] - 13, 20));
            constraints.insets = new Insets(5, 5, 5, 0);

        } else {
            fastaField.setPreferredSize(new Dimension(maximumSize[1] + 15, 20));
            constraints.insets = new Insets(5, 5, 5, 5);
        }
        if (parsingRuleFastasAreNotValid){

            changeJTextFieldLook(fastaField,SOFT_ERROR_COLOR,Color.WHITE,"The set of fasta rules is not valid");
            //fastaName.setForeground(new Color(200,0,0));
        }
        constraints.gridx++;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;


        displayParsingRule.add(fastaField, constraints);


        if (parsingRuleHasManyFastaRules) {

            JButton viewFastaButton = new JButton("...");
            viewFastaButton.setMargin(new Insets(0, 0, 2, 0));
            viewFastaButton.setToolTipText("Click to display the list of fasta rules");
            viewFastaButton.addActionListener(e -> {

                JList<String> fastaList = new JList<>(fastaNames.toArray(new String[0]));
                JScrollPane scrollPane = new JScrollPane(fastaList);
                ArrayList<Boolean> indicator=ParsingRulesTester.testFastaList(fastaNames);
                ListCellRenderer<? super String> cellRenderer = new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                        if (indicator.get(index).equals(true)) {
                            component.setForeground(Color.BLACK);
                            component.setBackground(new Color(215,215,215));
                        } else {
                            component.setForeground(Color.WHITE);
                            component.setBackground(new Color(180,0,0));
                        }

                        return component;
                    }
                };

                fastaList.setCellRenderer(cellRenderer);
                int visibleRowCount = Math.min(fastaList.getModel().getSize(), 9);

                fastaList.setVisibleRowCount(visibleRowCount);
                // Show the dialog box with the JList inside
                JOptionPane.showMessageDialog(fastaField, scrollPane, "List of fasta name rules", JOptionPane.PLAIN_MESSAGE);

            });

            constraints.gridx++;
            constraints.fill = GridBagConstraints.NONE;
            constraints.anchor = GridBagConstraints.EAST;
            constraints.weightx = 0;
            constraints.insets = new Insets(0, 2, 0, 5);

            displayParsingRule.add(viewFastaButton, constraints);
        }

        JLabel jLabelFastaVersion = new JLabel("Fasta version rule: ");
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        displayParsingRule.add(jLabelFastaVersion, constraints);

        JTextField fastaVersionRegExpTF = new JTextField(fastaVersion);
        fastaVersionRegExpTF.setEnabled(false);
        fastaVersionRegExpTF.setEditable(false);
        constraints.gridx++;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 5, 0, 5);
        fastaVersionRegExpTF.setPreferredSize(new Dimension(maximumSize[2] - 15, 20));
        displayParsingRule.add(fastaVersionRegExpTF, constraints);

        JLabel jLabelProtein = new JLabel("Protein accession rule: ");
        constraints.gridx++;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        constraints.insets = new Insets(0, 5, 0, 5);
        displayParsingRule.add(jLabelProtein, constraints);

        JTextField proteinField = new JTextField(protein);
        proteinField.setEnabled(false);
        proteinField.setEditable(false);
        constraints.gridx++;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        if (parsingRuleHasManyFastaRules)
            constraints.gridwidth = 2;
        proteinField.setPreferredSize(new Dimension(maximumSize[3], 20));
        if (!proteinAccessionRuleIsValid){

            changeJTextFieldLook(proteinField,SOFT_ERROR_COLOR,Color.WHITE,"The protein accession rule is not valid");

           // jLabelProtein.setForeground(new Color(180,0,0));

        }
        displayParsingRule.add(proteinField, constraints);

        constraints.gridwidth = parsingRuleHasManyFastaRules ? 5 : 4;
        constraints.gridy++;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(4, 2, 3, 4);
        displayParsingRule.add(setOfButtonPanel(parsingRule), constraints);

        return displayParsingRule;

    }

    public void updateValues() {
        // TODO Auto-generated method stub
        updatePanel();
        ConfigWindow.getInstance().pack();
    }

    // JPanel with two edit and suppress buttons for each parsing rule
    private JPanel setOfButtonPanel(ParsingRule parsingRule) {
        JPanel setOfButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JButton editButton = new JButton(IconManager.getIcon(IconManager.IconType.EDIT));
        editButton.setHorizontalAlignment(SwingConstants.LEFT);
        editButton.setToolTipText("Click to edit the parsing rule");
        editButton.setMargin(new Insets(1, 9, 1, 9));
        editButton.addActionListener(e -> editRule(parsingRule));
        JButton deleteButton = new JButton(IconManager.getIcon(IconManager.IconType.TRASH));
        deleteButton.setHorizontalAlignment(SwingConstants.LEFT);
        deleteButton.setToolTipText("Click to delete the parsing rule");
        deleteButton.setMargin(new Insets(1, 9, 1, 9));
        deleteButton.addActionListener(e -> deleteRule(parsingRule));
        gbc.insets = new Insets(0, 2, 0, 0);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        setOfButtonPanel.add(editButton, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        setOfButtonPanel.add(deleteButton, gbc);

        return setOfButtonPanel;

    }

    private JButton globalButtonTest() {
        JButton testButton = new JButton("Test");
        testButton.setIcon(IconManager.getIcon(IconManager.IconType.TEST));
        testButton.setEnabled(true);
        testButton.setToolTipText("Click to proceed to the test on fasta files");

        testButton.addActionListener(e -> {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    ParsingRulesTester.globalTest();
                    return null;
                }
            };

            worker.execute();
        });
        testButton.setSize(30, 30);
        return testButton;
    }


    private void deleteRule(ParsingRule parsingRuleToBeDeleted) {
        boolean deleteConfirmation = Popup.yesNoCenterToComponent(ConfigWindow.getInstance(), "Are you sure you want to delete this parsing rule?");
        if (deleteConfirmation) {
            boolean success = ConfigManager.getInstance().getParsingRulesManager().deleteRule(parsingRuleToBeDeleted);
            if (success) {
                updatePanel();
            }
        }
    }


    private void editRule(ParsingRule parsingRuleEdited) {

        List<ParsingRule> setOfRules = ConfigManager.getInstance().getParsingRulesManager().getSetOfRules();

        int index = setOfRules.indexOf(parsingRuleEdited);

        ParsingRuleEditDialog parsingRuleEditDialog = new ParsingRuleEditDialog(ConfigWindow.getInstance(), ParsingRuleEditDialog.TypeOfDialog.Edit, parsingRuleEdited);


        parsingRuleEditDialog.setSize(600, 380);
        parsingRuleEditDialog.centerToWindow(ConfigWindow.getInstance());
        parsingRuleEditDialog.setHelpHeader(IconManager.getIcon(IconManager.IconType.INFORMATION), "", "From this window you can modify the parsing rule" +
                ", you can also test if the protein rule does extract properly the accession of the protein.");
        parsingRuleEditDialog.setVisible(true);


        if (parsingRuleEditDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {

            ParsingRule modifiedParsingRule = parsingRuleEditDialog.getParsingRuleInsideDialog();

            ConfigManager.getInstance().getParsingRulesManager().updateSetOfRules(index, modifiedParsingRule);
            updatePanel();
        }




    }
    private void changeJTextFieldLook(JTextField jTextField, Color backGroundColor, Color textColor, String toolTipMessage){
        jTextField.setBackground(backGroundColor);
        if (!jTextField.isEnabled())
        {jTextField.setDisabledTextColor(textColor);}
        else {
            jTextField.setForeground(textColor);
        }
        jTextField.setToolTipText(toolTipMessage);
    }


}






