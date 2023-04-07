package fr.proline.zero.gui;

import fr.proline.studio.gui.DefaultDialog;
import fr.proline.zero.util.ParsingRule;
import fr.proline.zero.util.RegExUtil;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

public class TestDialog extends DefaultDialog {

  private StringBuilder sb;
    JTextField lineField;

    JTextField resultOfTest;
    String protein;

    public TestDialog(Window parent, JTextField label, JTextField fastaVersion, JTextField proteinRule, List<String> fastaList) {
        super(parent);
        this.setStatusVisible(true);
        this.setResizable(true);
        this.setInternalComponent(createTestPanel());
        this.protein=proteinRule.getText();
        super.pack();
    }
    protected static void parse(StringBuilder sb, String rule, String fieldName, String stringToParse) {
        sb.append(fieldName);
        String ruleTreat;
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

    private JPanel createTestPanel(){
        JPanel testPanel=new JPanel(new GridBagLayout());
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=0;
        JLabel entryLineLabel=new JLabel("Enter line to test");
        gbc.insets=new Insets(0,15,0,15);
        testPanel.add(entryLineLabel,gbc);
        gbc.gridx++;

        lineField=new JTextField();
        lineField.setPreferredSize(new Dimension(500,20));
        lineField.setEditable(true);
        lineField.setEnabled(true);

        testPanel.add(lineField,gbc);
        JButton testButton=new JButton("Test line");
        testButton.addActionListener(e -> {
           String prot= findProtName();
           if (prot!=null)
           { System.out.println(prot);
           resultOfTest.setText(prot);}
           else {
               resultOfTest.setText("Protein name could not be extracted using regular expresion: "+protein);
           }
        });
        gbc.gridx++;
        gbc.fill=GridBagConstraints.NONE;
        testPanel.add(testButton,gbc);
        gbc.gridx=1;
        gbc.gridy++;
       // gbc.fill=GridBagConstraints.HORIZONTAL;

        resultOfTest=new JTextField();
        resultOfTest.setEnabled(true);
        resultOfTest.setPreferredSize(new Dimension(500,20));
        testPanel.add(resultOfTest,gbc);


        return testPanel;
    }
    public String findProtName(){
        String foundEntry = RegExUtil.getMatchingString(lineField.getText(),protein );
        return foundEntry;

    }


}
