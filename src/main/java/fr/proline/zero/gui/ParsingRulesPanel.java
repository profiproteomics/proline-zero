package fr.proline.zero.gui;

import fr.proline.module.seq.service.FastaPathsScanner;
import fr.proline.studio.gui.DefaultDialog;
import fr.proline.studio.gui.InfoDialog;
import fr.proline.studio.utils.IconManager;
import fr.proline.zero.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.proline.module.seq.Constants.LATIN_1_CHARSET;
import static fr.proline.studio.gui.DefaultDialog.BUTTON_CANCEL;
import static fr.proline.studio.gui.DefaultDialog.BUTTON_OK;


public class ParsingRulesPanel extends JPanel {

    private static final Logger LOG = LoggerFactory.getLogger(ParsingRulesPanel.class);
    // private JTextField labelProt;


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
            add(new JLabel("Default protein accession rule: "), c);


            String protByDefault = ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule();
            JTextField labelProt = new JTextField(protByDefault);
            labelProt.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {

                    String textInsideTextField = labelProt.getText();
                    System.out.println("insertCalled");
                    ConfigManager.getInstance().setProteinByDefault(textInsideTextField);


                }

                @Override
                public void removeUpdate(DocumentEvent e) {

                    String textInsideTextField = labelProt.getText();
                    System.out.println("remove called");
                    ConfigManager.getInstance().setProteinByDefault(textInsideTextField);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    System.out.println("changed called");
                    // CD never called???

                }
            });
            c.anchor = GridBagConstraints.WEST;
            c.gridx++;
            // c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 0.5;
            labelProt.setPreferredSize(labelProt.getPreferredSize());
            add(labelProt, c);
            c.weightx = 1;
            c.gridx = 0;
            c.gridwidth = 2;
            c.gridy++;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.NORTHWEST;
            c.weighty = 0;
            //---------------
            // JPanel rulesListPanel=createParsingRulesListPanel();
            JScrollPane scrollPane = new JScrollPane(createParsingRulesListPanel());
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setPreferredSize(new Dimension(700, 550));
            scrollPane.setBorder(null);
            add(scrollPane, c);
            //---------------
            //add(createParsingRulesListPanel(), c);


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

    private void openAddDialog() {
        ParsingRuleEditDialog newDialog = new ParsingRuleEditDialog(ConfigWindow.getInstance(), ParsingRuleEditDialog.TypeOfDialog.Add, null);
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
        constraints.insets = new Insets(5, 1, 5, 1);
        constraints.weightx = 1;
        int[] maximas = getMaximums(setOfRules);

        for (int i = 0; i < setOfRules.size(); i++) {
            ParsingRule currentParsingRule = setOfRules.get(i);
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

    private int getSizeInPixelsButton(JButton button) {
        Font fontused = button.getFont();
        FontMetrics fontMetrics = button.getFontMetrics(fontused);
        String stringInsideButton = button.getText();
        return fontMetrics.stringWidth(stringInsideButton);

    }

    private List<Integer> getSizesGeneric(ParsingRule parsingRule) {

        List<Integer> sizes = new ArrayList<>(4);
        JTextField nameField = new JTextField(parsingRule.getName());
        JTextField fastaNameField = new JTextField(fastaConcatenator(parsingRule.getFastaNameRegExp()));
        boolean manyFastaRules = parsingRule.getFastaNameRegExp().size() > 2;
        int val = (manyFastaRules) ? 1 : 0;
        JButton button = new JButton("...");
        JTextField fastaVersionField = new JTextField(parsingRule.getFastaVersionRegExp());
        JTextField proteinAccField = new JTextField(parsingRule.getProteinAccRegExp());
        sizes.add(getSizeinPixels(nameField));
        sizes.add(getSizeinPixels(fastaNameField) + getSizeInPixelsButton(button) * val);
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

    // not used
    private JPanel viewer(ParsingRule parsingRule) {

        JPanel fastaviewer = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        String firstelements = parsingRule.getFastaNameRegExp().get(0);
        firstelements = firstelements + ";" + parsingRule.getFastaNameRegExp().get(1);
        JLabel viewer = new JLabel(firstelements);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fastaviewer.add(viewer, gbc);
        gbc.gridx++;
        JButton viewButton = new JButton(IconManager.getIcon(IconManager.IconType.TEST));
        viewButton.addActionListener(e -> {
            System.out.println("pressed!!!!");
            ParsingRuleEditDialog viewFastas = new ParsingRuleEditDialog(ConfigWindow.getInstance(), ParsingRuleEditDialog.TypeOfDialog.ViewFastas, parsingRule);
            viewFastas.setSize(new Dimension(350, 300));
            viewFastas.setLocationRelativeTo(null);
            viewFastas.setVisible(true);

        });
        gbc.fill = GridBagConstraints.NONE;
        fastaviewer.add(viewButton, gbc);
        return fastaviewer;
    }


    private JPanel displayParsingRules(ParsingRule parsingRule, int[] maximumSize, int index) {
        JPanel displayPr = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        displayPr.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.gridy = 0;
        constraints.gridx = 0;

        String label = parsingRule.getName();
        String fastaVersion = parsingRule.getFastaVersionRegExp();
        String protein = parsingRule.getProteinAccRegExp();
        List<String> fastaNames = parsingRule.getFastaNameRegExp();
        boolean parsingRuleHasManyFastasRules = (fastaNames.size() > 2);

        JLabel jLabelName = new JLabel("Label: ");
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        displayPr.add(jLabelName, constraints);

        JTextField labelField = new JTextField(label);
        labelField.setEnabled(parsingRule.isEditable());
        labelField.setPreferredSize(new Dimension(maximumSize[0] + 15, 20));


        constraints.gridx++;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        displayPr.add(labelField, constraints);

        JLabel fastaName = new JLabel("Fasta name rule: ");
        constraints.gridx++;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        displayPr.add(fastaName, constraints);

        JTextField fastaField = new JTextField(fastaConcatenator(fastaNames));
        fastaField.setEnabled(parsingRule.isEditable());
        fastaField.setEditable(parsingRule.isEditable());

        if (parsingRuleHasManyFastasRules) {
            fastaField.setPreferredSize(new Dimension(maximumSize[1] - 13, 20));
            constraints.insets = new Insets(5, 5, 5, 0);

        } else {
            fastaField.setPreferredSize(new Dimension(maximumSize[1] + 15, 20));
            constraints.insets = new Insets(5, 5, 5, 5);
        }
        constraints.gridx++;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        // fastaField.setPreferredSize(new Dimension(190,20));
        displayPr.add(fastaField, constraints);


        if (parsingRuleHasManyFastasRules) {

            JButton viewfastaButton = new JButton("...");
            viewfastaButton.setMargin(new Insets(0, 0, 2, 0));
            viewfastaButton.setToolTipText("Click to display the list of fasta rules");
            // viewfastaButton.setIcon(IconManager.getIcon(IconManager.IconType.TABLE));
            viewfastaButton.addActionListener(e -> {

                //-------------- Dialog Version------------------//
           /*    ParsingRuleEditDialog viewFastas = new ParsingRuleEditDialog(ConfigWindow.getInstance(), ParsingRuleEditDialog.TypeOfDialog.ViewFastas, parsingRule);
                viewFastas.setLocationRelativeTo(viewfastaButton);
                viewFastas.setVisible(true);*/


                // ----------------------JOptionPane Version----------------//
                JList<String> fastaList = new JList<>(fastaNames.toArray(new String[fastaNames.size()]));
                JScrollPane scrollPane = new JScrollPane(fastaList);

                // Show the dialog box with the JList inside with a JScrollPane
                JOptionPane.showMessageDialog(fastaField, scrollPane, "Fasta Names", JOptionPane.PLAIN_MESSAGE);


            });

            constraints.gridx++;
            constraints.fill = GridBagConstraints.NONE;
            constraints.anchor = GridBagConstraints.EAST;
            constraints.weightx = 0;
            constraints.insets = new Insets(0, 2, 0, 5);

            displayPr.add(viewfastaButton, constraints);
        }

        JLabel jLabelFastaVersion = new JLabel("Fasta version rule: ");
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
        constraints.insets = new Insets(0, 5, 0, 5);
        fastaVersionRegExpTF.setPreferredSize(new Dimension(maximumSize[2] - 15, 20));
        //constraints.insets=new Insets(0,0,0,0);
        displayPr.add(fastaVersionRegExpTF, constraints);

        JLabel jLabelProtein = new JLabel("Protein accession rule: ");
        constraints.gridx++;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        constraints.insets = new Insets(0, 5, 0, 5);
        displayPr.add(jLabelProtein, constraints);

        JTextField proteinField = new JTextField(protein);
        proteinField.setEnabled(parsingRule.isEditable());
        proteinField.setEditable(parsingRule.isEditable());
        constraints.gridx++;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        if (parsingRuleHasManyFastasRules)
            constraints.gridwidth = 2;
        proteinField.setPreferredSize(new Dimension(maximumSize[3], 20));
        displayPr.add(proteinField, constraints);

        constraints.gridwidth = parsingRuleHasManyFastasRules ? 5 : 4;
        constraints.gridy++;

        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(4, 2, 3, 4);
        displayPr.add(setOfButtonPanel(parsingRule), constraints);
        // add(Box.createHorizontalGlue());

        return displayPr;

    }

    public void updateValues() {
        // TODO Auto-generated method stub
        updatePanel();
        ConfigWindow.getInstance().pack();
    }

    // JPanel with two buttons for each parsing rule
    private JPanel setOfButtonPanel(ParsingRule parsingRule) {
        JPanel setOfButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JButton editButton = new JButton(IconManager.getIcon(IconManager.IconType.EDIT));
        editButton.setHorizontalAlignment(SwingConstants.LEFT);
        editButton.setToolTipText("Click to edit the parsing rule");
        editButton.setMargin(new Insets(1, 9, 1, 9));
        editButton.addActionListener(e -> {

            editRule(parsingRule);
        });
        JButton deleteButton = new JButton(IconManager.getIcon(IconManager.IconType.TRASH));
        deleteButton.setHorizontalAlignment(SwingConstants.LEFT);
        deleteButton.setToolTipText("Click to delete the parsing rule");
        deleteButton.setMargin(new Insets(1, 9, 1, 9));
        deleteButton.addActionListener(e -> {
            deleteRule(parsingRule);
        });
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
        testButton.addActionListener(e -> {

            try {
              //  globalTest();
                ConfigManager.getInstance().getParsingRulesTester().globalTest();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }


        });
        testButton.setSize(30, 30);
        return testButton;
    }

    private void globalTest() throws Exception {

        System.out.println("test pressed");
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, List<File>> fastaPaths = getFastaFilesMap();
        Set<Map.Entry<String, List<File>>> entries = fastaPaths.entrySet();
        boolean noResult = true;
        for (Map.Entry<String, List<File>> entry : entries) {
            String fastaName = entry.getKey();
            List<File> FastaFiles = entry.getValue();
            Object[] resultsOfTest = ParsingRule.getMatchingParsingRule(fastaName);
            ParsingRule rule = (ParsingRule) resultsOfTest[0];
            String protRegex;
            if (rule != null) {

                protRegex = rule.getProteinAccRegExp();
            } else {
                //TODO use current value or the one inside config file?
                protRegex = ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule();
            }
            //Read 3 entries in fasta files using ParsingRuleEntry regEx
            for (File nextFile : FastaFiles) {
                //stringBuilder.append("fasta file:  " + nextFile.getAbsolutePath());
                stringBuilder.append("Fasta file: " + nextFile.getName());
                stringBuilder.append("\n");

                if (resultsOfTest[1] == null) {
                    stringBuilder.append("No regular expression that match with the name of the file \n");
                } else {
                    stringBuilder.append("Parsing rule selected: " + ((ParsingRule) resultsOfTest[0]).getName() + "\n");
                    stringBuilder.append("Fasta Name RegEx:  " + resultsOfTest[1] + "\n");
                }
                // Warning curent or inside config file
                boolean defaultProtein = protRegex.equals(ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule());
                if (defaultProtein) {
                    stringBuilder.append("Default regular expression selected: " + protRegex + "\n");
                } else {
                    stringBuilder.append("Regular expression selected to do the test: " + protRegex + "\n");
                }

                BufferedReader br = null;

                try {
                    InputStream is = new FileInputStream(nextFile);
                    br = new BufferedReader(new InputStreamReader(is, LATIN_1_CHARSET));

                    String rawLine = br.readLine();
                    int countEntry = 0;
                    // int numberOfNoFoundEntry = 0;
                    boolean proteinHasBeenFound = false;
                    while (countEntry < 3 && rawLine != null) {

                        final String trimmedLine = rawLine.trim();
                        if (!trimmedLine.isEmpty() && trimmedLine.startsWith(">")) { //Found an entry
                            countEntry++;


                            String foundEntry = ParsingRulesUtils.getMatchingString(trimmedLine, protRegex);
                            if (foundEntry != null) {
                                proteinHasBeenFound = true;
                                stringBuilder.append(trimmedLine + "\n");
                                stringBuilder.append("protein name:  ");
                                stringBuilder.append(foundEntry);
                                stringBuilder.append("\n");
                                noResult = false;

                            }

                        } // End entryFound
                        rawLine = br.readLine();

                    }

                    if (!proteinHasBeenFound) {
                        stringBuilder.append("No protein found inside the file");
                        stringBuilder.append("\n");
                    }
                    stringBuilder.append("------------------------------------------------------------\n");

                    // End read some entries

                } finally {

                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException exClose) {
                            LOG.error("Error closing [" + nextFile + ']', exClose);
                        }
                    }

                }
            } //End go through associated fasta files


        }
        if (noResult) {
            stringBuilder.append("The test did not return any results you might check fasta files inside the folder panel");
        }

        System.out.println(stringBuilder);
        InfoDialog infoDialog = new InfoDialog(ConfigWindow.getInstance(), InfoDialog.InfoType.NO_ICON, "Test results", stringBuilder.toString(), false);
        infoDialog.setButtonVisible(BUTTON_OK, false);
        infoDialog.setButtonName(BUTTON_CANCEL, "close");
        infoDialog.setSize(1200, 500);
        infoDialog.centerToScreen();
        infoDialog.setVisible(true);


    }


    protected Map<String, List<File>> getFastaFilesMap() throws Exception {
        Map<String, List<File>> fastaFiles = null;
        List<String> localFASTAPaths = ConfigManager.getInstance().getParsingRulesManager().getFastaPaths();
        if (localFASTAPaths != null && !localFASTAPaths.isEmpty()) {
            fastaFiles = FastaPathsScanner.scanPaths(new FastaPathsScanner(), localFASTAPaths);

        } else {
            LOG.error("No valid localFASTAPaths configured");
        }
        return fastaFiles;
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

            ConfigManager.getInstance().getParsingRulesManager().updateSetOfRules(index, editedParsingRule);
            updatePanel();
        }

    }
}






