package fr.proline.zero.gui;

import fr.proline.studio.gui.DefaultDialog;


import javax.swing.*;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
/**
 * This class implements a dialog box used to do local tests on parsing rules
 * works but shall not be used because test is now done directly in ParsingRuleEditDialog
 * @deprecated 
 */

public class TestParsingRuleDialog extends DefaultDialog {



    private JTextField lineField;

    private JTextField proteinAccTField;

    private JTextField resultOfTest;

    private String protein;

    public TestParsingRuleDialog(Window parent, JTextField label, JTextField fastaVersion, JTextField proteinAccTField, List<String> fastaList) {
        super(parent);
        this.setStatusVisible(true);
        this.setTitle("Test parsing Rule");
        this.setResizable(true);
        this.protein = proteinAccTField.getText();
        this.setInternalComponent(createTestPanel(protein));
        super.pack();
    }

    protected static void parse(StringBuilder sb, String rule, String fieldName, String stringToParse) {
        sb.append(fieldName);

        String[] ruleList = rule.split("\\|\\|"); // split at "||"
        for (String element : ruleList) {
            if (!element.isEmpty()) {
                Pattern pattern = Pattern.compile(element);
                Matcher match = pattern.matcher(stringToParse);
                boolean findAMatch = match.find();
                if (findAMatch) {
                    String firstMatch = match.group(1);
                    sb.append(firstMatch);
                    break;//if first rule is match, don't do the next match
                }
            }
        }

        sb.append('\n');
    }

    private JPanel createTestPanel(String protein) {
        JPanel testPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.insets = new Insets(0, 15, 0, 15);

        JLabel proteinRegExLabel = new JLabel("Regular expression: ");
        testPanel.add(proteinRegExLabel, gbc);

        gbc.gridx++;
        proteinAccTField = new JTextField();
        proteinAccTField.setText(protein);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        testPanel.add(proteinAccTField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel entryLineLabel = new JLabel("Test");

        testPanel.add(entryLineLabel, gbc);
        gbc.gridx++;

        lineField = new JTextField();
        lineField.setPreferredSize(new Dimension(500, 20));
        lineField.setEditable(true);
        lineField.setEnabled(true);
        gbc.weightx = 1;
        testPanel.add(lineField, gbc);
        JButton testButton = new JButton("Test line");
        resultOfTest = new JTextField();
        testButton.addActionListener(e -> {

            String lineTested = lineField.getText();
            String proteinRegex = proteinAccTField.getText();

            if (!lineTested.equals("")) {
                String proteinNameExtracted = extractProteinNameWithRegEx(lineTested, proteinRegex);
                if (proteinNameExtracted != null) {
                    resultOfTest.setText(proteinNameExtracted);
                } else {
                    resultOfTest.setText("No protein accession extracted");
                }
            } else {
                highlight(lineField);
                setStatus(true, "Please enter a line from a fasta file ");
            }
        });
        gbc.gridx++;
        gbc.fill = GridBagConstraints.NONE;
        testPanel.add(testButton, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        testPanel.add(new JLabel("protein name extracted :"), gbc);

        resultOfTest.setEnabled(true);
        resultOfTest.setPreferredSize(new Dimension(500, 20));
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        testPanel.add(resultOfTest, gbc);
        return testPanel;
    }

    public String extractProteinNameWithRegEx(String line, String proteinRegex) {
        return getMatchingString(line, proteinRegex);
    }


    public static String getMatchingString(final String sourceText, final String searchStrRegEx) {
        if (sourceText == null || searchStrRegEx == null)
            return null;

        Pattern textPattern = Pattern.compile(searchStrRegEx, Pattern.CASE_INSENSITIVE);

        String result = null;
        if (textPattern != null) {
            final Matcher matcher = textPattern.matcher(sourceText);

            if (matcher.find()) {

                if (matcher.groupCount() >= 1)
                    result = matcher.group(1).trim();
            }
        }
        return result;
    }

    public String getNewProteinAccessionRule() {
        return proteinAccTField.getText();
    }


}

