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
import javax.swing.UIManager;

import static java.util.Objects.isNull;

/**
 * @author Christophe Delapierre
 * @see DefaultDialog
 * Used to display the results of the global test.
 * For each file display at most 3 lines tested and the name of the protein inside line
 */

public class ResultOfGlobalTestDialog extends DefaultDialog {

    public ResultOfGlobalTestDialog(Window parent, ArrayList<Object[]> resultStore, ArrayList<Map<String, String>> linesAndProteins,int successMatching,int numberOfFilesWithoutProtein) {

        super(parent);
        this.setResizable(true);
        this.setTitle("Global test results");
        this.setButtonVisible(BUTTON_HELP, false);
        this.setButtonVisible(BUTTON_CANCEL, false);
        this.setHelpHeader(IconManager.getIcon(IconManager.IconType.INFO_30),"Global test", SettingsConstant.PARSING_RULES_HELP_TEST_DIALOG);

        this.setInternalComponent(scrollPaneResult(resultStore, linesAndProteins,successMatching,numberOfFilesWithoutProtein));
        super.pack();

    }

    private JScrollPane scrollPaneResult(ArrayList<Object[]> resultStore, ArrayList<Map<String, String>> linesAndProteins, int successMatching,int numberOfFilesNoProtein) {
        JScrollPane scrollPane = new JScrollPane(createResultPanel(resultStore, linesAndProteins,successMatching,numberOfFilesNoProtein));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(900, 700));
        scrollPane.setBorder(null);
        return scrollPane;
    }


    private JPanel createResultPanel(ArrayList<Object[]> resultStore, ArrayList<Map<String, String>> linesAndProteins, int successMatching, int numberOfFilesNoProtein) {
        JPanel resultPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int numberOfResults = resultStore.size();

        String proteinByDefault = ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule();

        JPanel displayStats=displayStatsV2(numberOfResults,proteinByDefault,successMatching,numberOfFilesNoProtein);
        displayStats.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,2));
        resultPanel.add(displayStats,gbc);
        List<Integer> maximums = calculateMax(resultStore, linesAndProteins);

        gbc.gridy++;
        // TODO indicate if some fastaRegex used during the test where not valid Regex?
        for (int k = 0; k < numberOfResults; k++) {

            Object[] result = resultStore.get(k);
            Map<String, String> lines = linesAndProteins.get(k);
            gbc.ipadx = 5;
            gbc.ipady = 5;
            resultPanel.add(displayOneFileResult(result, lines,maximums), gbc);
            gbc.gridy++;
            JSeparator lineBar = new JSeparator();
            lineBar.setOrientation(SwingConstants.HORIZONTAL);
            resultPanel.add(lineBar, gbc);
            gbc.gridy++;

        }

        return resultPanel;


    }

    private JPanel displayStats(int totalNumberOfFiles,String proteinByDefault,int successMatching,int numberOfFilesNoProtein){

        JPanel displayStats=new JPanel(new GridBagLayout());
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(5,5,5,5);
        gbc.gridy=0;
        gbc.gridx=0;
        gbc.anchor=GridBagConstraints.WEST;
        gbc.weightx=1;
        JLabel numberOfFastaFiles=new JLabel("a total of: "+totalNumberOfFiles+" fasta files have beeen parsed during the test");
        displayStats.add(numberOfFastaFiles,gbc);
        JLabel proteinByDefrault=new JLabel("protein by default: "+proteinByDefault);
        gbc.gridy++;
        displayStats.add(proteinByDefrault,gbc);


        JLabel numberOfSuccess=new JLabel("A parsing rule has been found for "+successMatching+" fasta files");
        gbc.gridy++;
        displayStats.add(numberOfSuccess,gbc);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(97, 210, 115));

        double ratio = (double) successMatching / totalNumberOfFiles;
        int percentage = (int) (ratio * 100);

        progressBar.setValue(percentage);
        progressBar.setString(percentage + "% de match");
        progressBar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));


        gbc.gridy++;
        displayStats.add(progressBar, gbc);
        JLabel jLabelMatch=new JLabel("  parmi les fichiers fasta");
        displayStats.add(jLabelMatch,gbc);

        JProgressBar progressBarProtein = new JProgressBar(0, 100);
        progressBarProtein.setStringPainted(true);
        progressBarProtein.setForeground(new Color(97, 210, 115));

        double ratioProtein = (double) (totalNumberOfFiles-numberOfFilesNoProtein) / totalNumberOfFiles;
        int percentageProtein = (int) (ratioProtein * 100);

        progressBarProtein.setValue(percentageProtein);
        progressBarProtein.setString(percentageProtein + "% extractions");
        progressBarProtein.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));


        gbc.gridy++;
        displayStats.add(progressBarProtein, gbc);


        return displayStats;

    }
    private JPanel displayStatsV2(int totalNumberOfFiles, String proteinByDefault, int successMatching, int numberOfFilesNoProtein) {
        JPanel displayStats = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;

        JLabel numberOfFastaFiles = new JLabel("A total of " + totalNumberOfFiles + " fasta files have been parsed during the test");
        displayStats.add(numberOfFastaFiles, gbc);

        JLabel proteinByDefaultLabel = new JLabel("Protein by default: " + proteinByDefault);
        gbc.gridy++;
        displayStats.add(proteinByDefaultLabel, gbc);

        JLabel numberOfSuccess = new JLabel("A matching parsing rule has been found for " + successMatching + " fasta files");
        gbc.gridy++;
        displayStats.add(numberOfSuccess, gbc);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(85, 92, 128));

        double ratio = (double) successMatching / totalNumberOfFiles;
        int percentage = (int) (ratio * 100);

        progressBar.setValue(percentage);
       // progressBar.setString(percentage + "%");
        progressBar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel progressBarPanel = new JPanel(new BorderLayout());
        progressBarPanel.add(progressBar, BorderLayout.CENTER);

        JLabel jLabelMatch = new JLabel("  a parsing rule was found for "+percentage+"% of fasta files");
        progressBarPanel.add(jLabelMatch, BorderLayout.EAST);

        gbc.gridy++;
        displayStats.add(progressBarPanel, gbc);

        JProgressBar progressBarProtein = new JProgressBar(0, 100);
        progressBarProtein.setStringPainted(true);
        progressBarProtein.setForeground(new Color(85, 92, 128));


        double ratioProtein = (double) (totalNumberOfFiles - numberOfFilesNoProtein) / totalNumberOfFiles;
        int percentageProtein = (int) (ratioProtein * 100);

        progressBarProtein.setValue(percentageProtein);
        progressBarProtein.setString(percentageProtein + "% ");
        progressBarProtein.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel progressBarProteinPanel = new JPanel(new BorderLayout());
        progressBarProteinPanel.add(progressBarProtein, BorderLayout.CENTER);

        gbc.gridy++;
        displayStats.add(progressBarProteinPanel, gbc);

        JLabel jLabelExtractions = new JLabel("  a protein accession has been found in "+percentageProtein+"% of the files parsed");
        progressBarProteinPanel.add(jLabelExtractions, BorderLayout.EAST);


        return displayStats;
    }


    private JPanel displayOneFileResult(Object[] results, Map<String, String> lines,List<Integer> maximums) {
        JPanel displayOneResult = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        displayOneResult.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        ParsingRule parsingRule = (ParsingRule) results[0];

        String parsingRuleName = null;

        if (!isNull(parsingRule)) {
            parsingRuleName = parsingRule.getName();
        }
        String fastaFileName = (String) results[2];
        String fastaRegex=(String) results[1];
        JLabel jLabelName = new JLabel("Fasta file:  ");
        gbc.fill = GridBagConstraints.NONE;
       // gbc.anchor = GridBagConstraints.WEST;
        gbc.anchor=GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.ipadx = 5;
        displayOneResult.add(jLabelName, gbc);
        JTextField jTextFieldName = new JTextField();
        jTextFieldName.setText(fastaFileName);
        jTextFieldName.setEditable(false);
        jTextFieldName.setEnabled(false);
        jTextFieldName.setPreferredSize(new Dimension(maximums.get(0),20));
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
            jTextFieldParsingRuleLabel.setText(parsingRuleName+"  "+fastaRegex);
        } else {
            jTextFieldParsingRuleLabel.setText("No parsing rule found, will use " + ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule());
        }
        jTextFieldParsingRuleLabel.setEnabled(false);
        jTextFieldParsingRuleLabel.setEditable(false);
        jTextFieldParsingRuleLabel.setPreferredSize(new Dimension(maximums.get(1),20));
        gbc.gridx++;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        displayOneResult.add(jTextFieldParsingRuleLabel, gbc);



        for (Map.Entry<String, String> entry : lines.entrySet()) {
            JLabel jLabelLine = new JLabel("Fasta entry:");
            // gbc.ipadx=0;
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.NONE;
           // gbc.anchor = GridBagConstraints.WEST;
            gbc.anchor=GridBagConstraints.EAST;
            displayOneResult.add(jLabelLine, gbc);
            String key = entry.getKey();
            int numberOfChars = key.length();
            String lineTrimmed = key.substring(0, Math.min(numberOfChars, 55));
            String proteinName = entry.getValue();

            JTextField lineTextField = new JTextField(lineTrimmed);
            lineTextField.setEnabled(false);
            lineTextField.setPreferredSize(new Dimension(maximums.get(2),20));
            gbc.gridx++;
            gbc.weightx = 1;
            // gbc.gridwidth=2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.EAST;
            displayOneResult.add(lineTextField, gbc);
            JLabel proteinLabel = new JLabel("protein accession extracted:");
            gbc.gridx++;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            displayOneResult.add(proteinLabel, gbc);
            JTextField proteinExtracted = new JTextField(proteinName);
            proteinExtracted.setEnabled(false);
            proteinExtracted.setPreferredSize(new Dimension(maximums.get(3),20));
            gbc.gridx++;
            //gbc.gridwidth=1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            // gbc.ipadx=5;
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
     *
     * Calculate maximum values in pixels of the JTextfields inside ResultGlobalTestDialog
     * * @param resultStore
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
                String fastaRegEx=(String) resultStore.get(k)[1];
                JTextField parsingRuleTest = new JTextField(parsingRuleName+" "+fastaRegEx);
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
