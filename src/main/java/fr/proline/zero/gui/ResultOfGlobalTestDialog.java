package fr.proline.zero.gui;

import fr.proline.studio.gui.DefaultDialog;
import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.ParsingRule;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * @see  DefaultDialog
 * Used to display the results of the global test.
 * For each file display at most 3 lines tested and the name of the protein inside line
 * @author Christophe Delapierre
 */

public class ResultOfGlobalTestDialog extends DefaultDialog {

    private ArrayList<Object[]> resultStore;
    private ArrayList<Map<String,String>> linesAndProteins;

    public ResultOfGlobalTestDialog(Window parent, ArrayList<Object[]> resultStore, ArrayList<Map<String,String>> linesAndProteins) {

        super(parent);
        this.resultStore=resultStore;
        this.linesAndProteins=linesAndProteins;
        this.setResizable(true);
        this.setSize(800,700);
        setInternalComponent(scrollPaneResult(resultStore,linesAndProteins));

    }

    private JScrollPane scrollPaneResult(ArrayList<Object[]> resultStore,ArrayList<Map<String,String>> linesAndProteins){
        JScrollPane scrollPane=new JScrollPane(createResultPanel(resultStore,linesAndProteins));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(800, 700));
        scrollPane.setBorder(null);
        return scrollPane;
    }


    private JPanel createResultPanel(ArrayList<Object[]> resultStore,ArrayList<Map<String,String>> linesAndProteins) {
        JPanel resultPanel=new JPanel(new GridBagLayout());
        GridBagConstraints gbc= new GridBagConstraints();
        gbc.insets=new Insets(5,5,5,5);
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.weightx=1;

        int numberOfResults=resultStore.size();
        String numberAsAString=String.valueOf(resultStore.size());
        String protByDefault=ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule();
        JLabel numberJLabel=new JLabel("Total number of fasta files parsed: "+numberAsAString+ "      protein by default: "+protByDefault);
        resultPanel.add(numberJLabel,gbc);

        gbc.gridy++;
        // TODO indicate if some fastaRegex used during the test where not valid Regex?
        for (int k=0;k<numberOfResults;k++){

            Object[] result=resultStore.get(k);
            Map<String,String> lines=linesAndProteins.get(k);
            gbc.ipadx=5;
            gbc.ipady=5;
            resultPanel.add(displayOneFileResult(result,lines),gbc);
            gbc.gridy++;
            JSeparator lineBar=new JSeparator();
            lineBar.setOrientation(SwingConstants.HORIZONTAL);
            resultPanel.add(lineBar,gbc);
            gbc.gridy++;

        }

        return resultPanel;


    }

    private JPanel displayOneFileResult(Object[] results, Map<String,String> lines){
        JPanel displayOneResult=new JPanel(new GridBagLayout());
        GridBagConstraints gbc=new GridBagConstraints();
        displayOneResult.setBorder(BorderFactory.createLineBorder(Color.darkGray,1));

        gbc.insets=new Insets(5,5,5,5);
        gbc.gridx=0;
        gbc.gridy=0;
        ParsingRule  parsingRule= (ParsingRule) results[0];
        String fastaRegEx= (String) results[1];
        String fastaFilename= (String) results[2];
        JLabel jLabelName=new JLabel("file:  ");
        gbc.fill=GridBagConstraints.NONE;
        gbc.anchor=GridBagConstraints.WEST;
        gbc.weightx=0;
        gbc.ipadx=5;
        displayOneResult.add(jLabelName,gbc);
        JTextField jTextFieldName=new JTextField();
        jTextFieldName.setText(fastaFilename);
        jTextFieldName.setEditable(false);
        jTextFieldName.setEnabled(false);
        gbc.gridx++;
        gbc.weightx=0.5;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        displayOneResult.add(jTextFieldName,gbc);

        JLabel jLabelRegExName=new JLabel("Fasta name RegEx:  ");
        gbc.gridx++;
        gbc.weightx=0;
        gbc.fill=GridBagConstraints.NONE;
        displayOneResult.add(jLabelRegExName,gbc);
        JTextField jTextFieldFastaRegEx=new JTextField();
        if (fastaRegEx!=null)
        { jTextFieldFastaRegEx.setText(fastaRegEx);}
        else {
            jTextFieldFastaRegEx.setText("No parsing rule found, will use "+ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule());
        }
        jTextFieldFastaRegEx.setEnabled(false);
        jTextFieldFastaRegEx.setEditable(false);
        gbc.gridx++;
        gbc.weightx=1;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        displayOneResult.add(jTextFieldFastaRegEx,gbc);
        /*gbc.gridx++;
        gbc.weightx=1;
        gbc.fill=GridBagConstraints.NONE;
        displayOneResult.add(paramsOfTest(fastaRegEx,parsingRule),gbc);*/

        int numberOfLines=lines.size();

        for (Map.Entry<String, String> entry : lines.entrySet()) {
            JLabel jLabelLine=new JLabel("Line: ");
           // gbc.ipadx=0;
            gbc.gridx=0;
            gbc.gridy++;
            gbc.weightx=0;
            gbc.fill=GridBagConstraints.NONE;
            gbc.anchor=GridBagConstraints.WEST;
            displayOneResult.add(jLabelLine,gbc);
            String key = entry.getKey();
            int numberOfChars=key.length();
            String lineTrimmed = key.substring(0, Math.min(numberOfChars,40));
            String proteinName = entry.getValue();

            JTextField lineTextField=new JTextField(lineTrimmed);
            lineTextField.setEnabled(false);
            gbc.gridx++;
            gbc.weightx=1;
           // gbc.gridwidth=2;
            gbc.fill=GridBagConstraints.HORIZONTAL;
            gbc.anchor=GridBagConstraints.EAST;
            displayOneResult.add(lineTextField,gbc);
            JLabel proteinLabel=new JLabel("protein name extracted");
            gbc.gridx++;
            gbc.fill=GridBagConstraints.NONE;
            gbc.anchor=GridBagConstraints.WEST;
            displayOneResult.add(proteinLabel,gbc);
            JTextField proteinExtracted=new JTextField(proteinName);
            proteinExtracted.setEnabled(false);
            gbc.gridx++;
           //gbc.gridwidth=1;
            gbc.fill=GridBagConstraints.HORIZONTAL;
           // gbc.ipadx=5;
            gbc.anchor=GridBagConstraints.CENTER;
            gbc.ipadx=5;
            displayOneResult.add(proteinExtracted,gbc);

        }
        return displayOneResult;

    }
    private JPanel createResultPanelOLD(ArrayList<Object[]> resultStore) {
        JPanel resultPanel=new JPanel(new GridBagLayout());
        GridBagConstraints gbc= new GridBagConstraints();
        gbc.gridx=0;
        gbc.gridy=0;

        int numberOfResults=resultStore.size();
        for (int k=0;k<numberOfResults;k++){

            Object[] result=resultStore.get(k);

            ParsingRule  parsingRule= (ParsingRule) result[0];
            String fastaRegEx= (String) result[1];
            String fastaFilename= (String) result[2];

            JLabel jlabel;
            if (fastaRegEx!=null)
            { jlabel=new JLabel("fasta rule selected: "+fastaRegEx+" for fastaFile: "+fastaFilename);
            }
            else {
                jlabel=new JLabel("No fasta rule found for that file: "+fastaFilename);
            }
            gbc.gridy++;

            resultPanel.add(jlabel,gbc);


            if (parsingRule != null) {
                JLabel parsingLabel=new JLabel("Parsing rule selected:  "+parsingRule.getName());
                gbc.gridy++;
                resultPanel.add(parsingLabel,gbc);

            }
            else {
                JLabel parsinglabel=new JLabel("Default protein used");
                gbc.gridy++;
                resultPanel.add(parsinglabel,gbc);
            }
            gbc.gridx=0;

        }

        return resultPanel;


    }
    // will contain all the parameters to do the test on a file
    private JPanel paramsOfTest(String fastaRegEx,ParsingRule parsingRule){
        JPanel paramsOfTest=new JPanel(new GridLayout(1,4));
        JLabel jLabelRegExName=new JLabel("Fasta name RegEx:  ");
        paramsOfTest.add(jLabelRegExName);
        JTextField jTextFieldFastaRegEx=new JTextField();
        if (fastaRegEx!=null)
        { jTextFieldFastaRegEx.setText(fastaRegEx);}
        else {
            jTextFieldFastaRegEx.setText(ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule());
        }
        jTextFieldFastaRegEx.setEnabled(false);
        jTextFieldFastaRegEx.setEditable(false);
        paramsOfTest.add(jTextFieldFastaRegEx);
        JLabel jLabelParsingRuleName=new JLabel("Parsing Rule: ");
        paramsOfTest.add(jLabelParsingRuleName);
        JTextField jTextFieldParsingRuleName=new JTextField();
        if (parsingRule!=null){
            jTextFieldParsingRuleName.setText(parsingRule.getName());
        }
        else {
            jTextFieldParsingRuleName.setText("No parsing rule found for that file");
        }
        paramsOfTest.add(jTextFieldParsingRuleName);

        return paramsOfTest;



    }
}
