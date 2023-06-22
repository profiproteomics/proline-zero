package fr.proline.zero.gui;

import fr.proline.studio.gui.DefaultDialog;
import fr.proline.studio.utils.IconManager;
import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.ParsingRule;
import fr.proline.zero.util.SettingsConstant;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

/**
 * @author Christophe Delapierre
 * @see DefaultDialog
 * Used to display the results of the global test.
 * For each file display at most 3 lines tested and the accession of the protein inside line if it matches with
 * protein accession regex
 */

public class ResultOfGlobalTestDialog extends DefaultDialog {

    private final static Color SOFT_ERROR_COLOR = new Color(212, 133, 175);
    private final static Color SUCCESS_COLOR = new Color(174, 203, 170);

    public ResultOfGlobalTestDialog(Window parent, ArrayList<Object[]> resultStore, ArrayList<Map<String, String>> linesAndProteins, int successMatching, int numberOfFilesWithoutProtein) {

        super(parent);
        this.setTitle("Global test results");
        this.setButtonVisible(BUTTON_HELP, false);
        this.setButtonVisible(BUTTON_CANCEL, false);
        this.setHelpHeader(IconManager.getIcon(IconManager.IconType.INFORMATION), "Global test", SettingsConstant.PARSING_RULES_HELP_TEST_DIALOG);

       // this.setInternalComponent(scrollPaneResult(resultStore, linesAndProteins, successMatching, numberOfFilesWithoutProtein));
        this.setInternalComponent(createGlobalPanel(resultStore, linesAndProteins, successMatching, numberOfFilesWithoutProtein));
        this.setStatusVisible(true);
        this.setResizable(true);
        this.setIconImage(IconManager.getImage(IconManager.IconType.INFORMATION));


    }

    public void pack() {

        // Do nothing on pack to avoid wrong sizing in CenterToWindow

    }

    private JScrollPane scrollPaneResult(ArrayList<Object[]> resultStore, ArrayList<Map<String, String>> linesAndProteins, int successMatching, int DefaultProteinNumberOfFiles) {
        JScrollPane scrollPane = new JScrollPane(createResultPanel(resultStore, linesAndProteins, successMatching, DefaultProteinNumberOfFiles));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        scrollPane.setBorder(null);
        return scrollPane;
    }
    private JPanel createGlobalPanel(ArrayList<Object[]> resultStore, ArrayList<Map<String, String>> linesAndProteins, int successMatching, int DefaultProteinNumberOfFiles){
        JPanel globalJPanel=new JPanel(new GridBagLayout());
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(5,5,5,5);
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.weighty=0;
        gbc.weightx=1;
        int numberOfResults= resultStore.size();
        String proteinByDefault=ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule();
        JPanel displayStats = displayStats(numberOfResults, proteinByDefault, successMatching, DefaultProteinNumberOfFiles);
        displayStats.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        displayStats.setBorder(BorderFactory.createTitledBorder(" Statistics "));
        globalJPanel.add(displayStats,gbc);
        JScrollPane scrollPaneResult=scrollPaneResult( resultStore,  linesAndProteins, successMatching, DefaultProteinNumberOfFiles);
        gbc.gridy++;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.weighty=1;
        gbc.weightx=1;
        globalJPanel.add(scrollPaneResult,gbc);
        return globalJPanel;
    }


    private JPanel createResultPanel(ArrayList<Object[]> resultStore, ArrayList<Map<String, String>> linesAndProteins, int successMatching, int DefaultProteinNumberOfFiles) {
        JPanel resultPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int numberOfResults = resultStore.size();

        String proteinByDefault = ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule();
/*
        JPanel displayStats = displayStats(numberOfResults, proteinByDefault, successMatching, DefaultProteinNumberOfFiles);
        displayStats.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        displayStats.setBorder(BorderFactory.createTitledBorder(" Statistics "));
        resultPanel.add(displayStats, gbc);*/
        List<Integer> maximums = calculateMax(resultStore, linesAndProteins);

       // gbc.gridy++;

        for (int k = 0; k < numberOfResults; k++) {

            Object[] result = resultStore.get(k);
            Map<String, String> lines = linesAndProteins.get(k);
            gbc.ipadx = 5;
            gbc.ipady = 5;
            resultPanel.add(displayOneFileResult(result, lines, maximums), gbc);
            gbc.gridy++;
            JSeparator lineBar = new JSeparator();
            lineBar.setOrientation(SwingConstants.HORIZONTAL);
            resultPanel.add(lineBar, gbc);
            gbc.gridy++;

        }

        return resultPanel;


    }
   /* private JPanel createResultPanel(ArrayList<Object[]> resultStore, ArrayList<Map<String, String>> linesAndProteins, int successMatching, int DefaultProteinNumberOfFiles) {
        JPanel resultPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int numberOfResults = resultStore.size();

        String proteinByDefault = ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule();

        JPanel displayStats = displayStats(numberOfResults, proteinByDefault, successMatching, DefaultProteinNumberOfFiles);
        displayStats.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        displayStats.setBorder(BorderFactory.createTitledBorder(" Statistics "));
        resultPanel.add(displayStats, gbc);
        List<Integer> maximums = calculateMax(resultStore, linesAndProteins);

        gbc.gridy++;

        for (int k = 0; k < numberOfResults; k++) {

            Object[] result = resultStore.get(k);
            Map<String, String> lines = linesAndProteins.get(k);
            gbc.ipadx = 5;
            gbc.ipady = 5;
            resultPanel.add(displayOneFileResult(result, lines, maximums), gbc);
            gbc.gridy++;
            JSeparator lineBar = new JSeparator();
            lineBar.setOrientation(SwingConstants.HORIZONTAL);
            resultPanel.add(lineBar, gbc);
            gbc.gridy++;

        }

        return resultPanel;


    }*/

    /**
     * @param totalNumberOfFiles
     * @param proteinByDefault
     * @param successMatching
     * @param DefaultProteinNumberOfFiles
     * @return a JPanel with some statistics about the test
     */
    private JPanel displayStats(int totalNumberOfFiles, String proteinByDefault, int successMatching, int DefaultProteinNumberOfFiles) {
        JPanel displayStats = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        double ratio = (double) successMatching / totalNumberOfFiles;
        int percentage = (int) (ratio * 100);

        JLabel numberOfFastaFiles = new JLabel( totalNumberOfFiles + " fasta files scanned during the test");
        displayStats.add(numberOfFastaFiles, gbc);

        JLabel proteinByDefaultLabel = new JLabel("Default protein accession rule: " + proteinByDefault + "  used for " + (100 - percentage) + " % of files");
        gbc.gridy++;
        displayStats.add(proteinByDefaultLabel, gbc);


        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(85, 92, 128));


        progressBar.setValue(percentage);

        progressBar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel progressBarPanel = new JPanel(new BorderLayout());
        progressBarPanel.add(progressBar, BorderLayout.CENTER);

        JLabel jLabelMatch = new JLabel("   parsing rule was found for " + percentage + "% of fasta files");
        progressBarPanel.add(jLabelMatch, BorderLayout.EAST);

        gbc.gridy++;
        displayStats.add(progressBarPanel, gbc);

        JProgressBar progressBarProtein = new JProgressBar(0, 100);
        progressBarProtein.setStringPainted(true);
        progressBarProtein.setForeground(new Color(85, 92, 128));


        double ratioProtein = (double) (totalNumberOfFiles - DefaultProteinNumberOfFiles) / totalNumberOfFiles;
        int percentageProtein = (int) (ratioProtein * 100);

        progressBarProtein.setValue(percentageProtein);
        progressBarProtein.setString(percentageProtein + "% ");
        progressBarProtein.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel progressBarProteinPanel = new JPanel(new BorderLayout());
        progressBarProteinPanel.add(progressBarProtein, BorderLayout.CENTER);

        gbc.gridy++;
        displayStats.add(progressBarProteinPanel, gbc);

        JLabel jLabelExtractions = new JLabel("   protein accession has been extracted in " + percentageProtein + "% of the files parsed");
        progressBarProteinPanel.add(jLabelExtractions, BorderLayout.EAST);


        return displayStats;
    }

    /**
     * builds a JPanel for a particular fasta file tested
     *
     * @param results
     * @param lines
     * @param maximums
     * @return
     */
    private JPanel displayOneFileResult(Object[] results, Map<String, String> lines, List<Integer> maximums) {
        JPanel displayOneResult = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        displayOneResult.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        ParsingRule parsingRule = (ParsingRule) results[0];


        String parsingRuleName = null;
        // Check at first if some lines have been extracted
        if (lines.containsValue("No line no protein accession extracted")) {
            displayOneResult.add(new JLabel(IconManager.getIcon(IconManager.IconType.CANCEL)), gbc);
            gbc.gridx++;
            gbc.anchor = GridBagConstraints.WEST;
            displayOneResult.add(new JLabel("This file might not be in fasta format"), gbc);

        } else {

            // in case a parsing rule has been found
            if (!isNull(parsingRule)) {

                parsingRuleName = parsingRule.getName();
                if (!lines.containsValue("No protein accession extracted")) {
                    displayOneResult.add(new JLabel(IconManager.getIcon(IconManager.IconType.TICK_CIRCLE)), gbc);
                    gbc.gridx++;
                    gbc.anchor = GridBagConstraints.WEST;
                    displayOneResult.add(new JLabel("Parsing rule found and protein accession has been extracted"), gbc);
                } else {
                    displayOneResult.add(new JLabel(IconManager.getIcon(IconManager.IconType.WARNING)), gbc);
                    gbc.gridx++;
                    gbc.anchor = GridBagConstraints.WEST;
                    displayOneResult.add(new JLabel("Parsing rule found but no protein accession found"), gbc);
                }
            }
            // in case no parsing rule has been found
            else {

                if (!lines.containsValue(("No protein accession extracted"))) {

                    displayOneResult.add(new JLabel(IconManager.getIcon(IconManager.IconType.TICK_CIRCLE)), gbc);
                    gbc.gridx++;
                    gbc.anchor = GridBagConstraints.WEST;
                    displayOneResult.add(new JLabel("No parsing rule found but protein accession has been extracted"), gbc);


                } else {

                    displayOneResult.add(new JLabel(IconManager.getIcon(IconManager.IconType.WARNING)), gbc);
                    gbc.gridx++;
                    gbc.anchor = GridBagConstraints.WEST;
                    displayOneResult.add(new JLabel("No parsing rule found and no protein accession has been extracted "), gbc);


                }


            }
        }
        String fastaFileName = (String) results[2];
        String fastaRegex = (String) results[1];
        JLabel jLabelName = new JLabel("Fasta file:  ");
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy++;
        gbc.gridx = 0;

        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.ipadx = 5;
        displayOneResult.add(jLabelName, gbc);
        JTextField jTextFieldName = new JTextField();

        jTextFieldName.setEditable(false);
        jTextFieldName.setEnabled(false);
        jTextFieldName.setPreferredSize(new Dimension(maximums.get(0), 20));


        jTextFieldName.setText(fastaFileName);
        gbc.gridx++;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        displayOneResult.add(jTextFieldName, gbc);

        JLabel jLabelParsingRuleUsed = new JLabel("Used parsing rule: ");
        gbc.gridx++;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        displayOneResult.add(jLabelParsingRuleUsed, gbc);
        JTextField jTextFieldParsingRuleLabel = new JTextField();
        if (!isNull(parsingRule)) {
            jTextFieldParsingRuleLabel.setText(parsingRuleName + " - " + fastaRegex);
        } else {

            jTextFieldParsingRuleLabel.setText("No parsing rule found , will use protein by default");
        }
        jTextFieldParsingRuleLabel.setEnabled(false);
        jTextFieldParsingRuleLabel.setEditable(false);
        jTextFieldParsingRuleLabel.setPreferredSize(new Dimension(maximums.get(1), 20));
        gbc.gridx++;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        displayOneResult.add(jTextFieldParsingRuleLabel, gbc);


        for (Map.Entry<String, String> entry : lines.entrySet()) {
            JLabel jLabelLine = new JLabel("Fasta entry:");

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.NONE;

            gbc.anchor = GridBagConstraints.EAST;
            displayOneResult.add(jLabelLine, gbc);
            String key = entry.getKey();
            int numberOfChars = key.length();
            String lineTrimmed = key.substring(0, Math.min(numberOfChars, 55));
            String proteinName = entry.getValue();

            JTextField lineTextField = new JTextField(lineTrimmed);
            lineTextField.setEnabled(false);
            lineTextField.setPreferredSize(new Dimension(maximums.get(2), 20));
            gbc.gridx++;
            gbc.weightx = 1;

            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.EAST;
            displayOneResult.add(lineTextField, gbc);
            JLabel proteinLabel = new JLabel("Protein accession extracted:");
            gbc.gridx++;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            displayOneResult.add(proteinLabel, gbc);
            JTextField proteinExtracted = new JTextField(proteinName);
            proteinExtracted.setEnabled(false);
            proteinExtracted.setPreferredSize(new Dimension(maximums.get(3), 20));
            gbc.gridx++;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.ipadx = 5;
            displayOneResult.add(proteinExtracted, gbc);

        }
        return displayOneResult;

    }


    private int getSizeInPixels(JTextField jTextField) {
        Font fontUsed = jTextField.getFont();
        FontMetrics fontMetrics = jTextField.getFontMetrics(fontUsed);
        String stringInTextField = jTextField.getText();
        return fontMetrics.stringWidth(stringInTextField);

    }

    /**
     * Calculate maximum sizes of the JTextfields inside ResultGlobalTestDialog
     * Used to harmonize the dimensions
     * * @param resultStore
     *
     * @param linesAndProteins
     * @return
     */

    private List<Integer> calculateMax(ArrayList<Object[]> resultStore, ArrayList<Map<String, String>> linesAndProteins) {

        List<Integer> maximums = new ArrayList<>();

        int numberOfResults = resultStore.size();
        int maxFastaFiles = 0;
        int maxParsingRuleName = 0;
        int maxEntryLine = 0;
        int maxProteinAccession = 0;

        for (int k = 0; k < numberOfResults; k++) {
            String fastaName = (String) resultStore.get(k)[2];
            JTextField testTextField = new JTextField(fastaName);
            maxFastaFiles = Math.max(maxFastaFiles, getSizeInPixels(testTextField));
            ParsingRule parsingRule = (ParsingRule) resultStore.get(k)[0];
            if (parsingRule != null) {

                String parsingRuleName = parsingRule.getName();
                String fastaRegEx = (String) resultStore.get(k)[1];
                JTextField parsingRuleTest = new JTextField(parsingRuleName + " " + fastaRegEx);
                maxParsingRuleName = Math.max(maxParsingRuleName, getSizeInPixels(parsingRuleTest));


            } else {
                String ParsingRuleName = "No parsing rule found, will use " + ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule();
                JTextField parsingRuleTest = new JTextField(ParsingRuleName);
                maxParsingRuleName = Math.max(maxParsingRuleName, getSizeInPixels(parsingRuleTest));
            }


            Map<String, String> lines = linesAndProteins.get(k);
            for (Map.Entry<String, String> entry : lines.entrySet()) {

                String key = entry.getKey();
                int numberOfChars = key.length();
                String lineTrimmed = key.substring(0, Math.min(numberOfChars, 55));
                String proteinName = entry.getValue();

                JTextField lineTextField = new JTextField(lineTrimmed);
                maxEntryLine = Math.max(maxEntryLine, getSizeInPixels(lineTextField));


                JTextField proteinExtracted = new JTextField(proteinName);
                maxProteinAccession = Math.max(maxProteinAccession, getSizeInPixels(proteinExtracted));

            }

        }
        maximums.add(0, maxFastaFiles);
        maximums.add(1, maxParsingRuleName);
        maximums.add(2, maxEntryLine);
        maximums.add(3, maxProteinAccession);
        return maximums;


    }
}
