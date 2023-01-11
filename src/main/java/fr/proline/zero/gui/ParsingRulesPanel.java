package fr.proline.zero.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

import fr.proline.zero.util.*;

public class ParsingRulesPanel extends JPanel {
    private JTextField labelField;
    private JTextField accessionParseRuleField;
    private JTextField fastaPatternField;
    private JTextField fastaVersionField;
    private Component filler;
    private JPanel addParsingRules;

    private GridBagConstraints c;

    public ArrayList<String> buildtestlist(){
        ArrayList<String> gfg = new ArrayList<String>();
        gfg.add("Geeks");
        gfg.add("for");
        gfg.add("Geeks");
        return gfg;

    }



    public ParsingRulesPanel() {

        initialize();

    }

    private void initialize() {
        // creation du layout
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 1;

        // creation des widgets

        JTextArea help = new JTextArea();
        help.setMinimumSize(new Dimension(300, 75));
        help.setPreferredSize(new Dimension(300, 75));
        help.setText(SettingsConstant.PARSING_RULES_HELP_PANE);
        help.setEditable(false);

        // ajout des widgets au layout
        c.gridx = 0;
        c.gridy = 0;
        add(help, c);

        c.insets = new java.awt.Insets(20, 15, 0, 15);
        c.gridy++;
        add(createParsingRulesPanel(), c);
        c.gridy++;
        add(displaysetOfRules(),c);

        c.fill = GridBagConstraints.VERTICAL;
        c.weighty = 1;
        add(Box.createHorizontalGlue(), c);
        revalidate();
        repaint();
    }

    private void updatePanel() {
        removeAll();
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 1;

        // creation des widgets

        JTextArea help = new JTextArea();
        help.setMinimumSize(new Dimension(300, 75));
        help.setPreferredSize(new Dimension(300, 75));
        help.setText(SettingsConstant.PARSING_RULES_HELP_PANE);
        help.setEditable(false);

        // ajout des widgets au layout
        c.gridx = 0;
        c.gridy = 0;
        add(help, c);

        c.insets = new java.awt.Insets(20, 15, 0, 15);
        c.gridy++;
        add(createParsingRulesPanel(), c);
        c.gridy++;
        add(displaysetOfRules(),c);

        c.fill = GridBagConstraints.VERTICAL;
        c.weighty = 1;
        add(Box.createHorizontalGlue(), c);
        revalidate();
        repaint();

    }

    private JPanel createParsingRulesPanel() {
        // creation du panel et du layout
        addParsingRules = new JPanel(new GridBagLayout());
        addParsingRules.setBorder(BorderFactory.createTitledBorder("Parsing rules"));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new java.awt.Insets(5, 5, 5, 5);
        c.gridwidth = 2;

        // creation des elements
        JButton plus = new JButton("+");
        try {
            Icon plusIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("plus.png")));
            plus.setText("");
            plus.setIcon(plusIcon);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        plus.addActionListener(addRule());
        // ajout des widgets au layout
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        addParsingRules.add(new JLabel("Add a parsing rule : "), c);

        c.gridy++;
        addParsingRules.add(createParsingRuleFieldsPanel(), c);

        c.gridwidth = 1;
        c.gridy++;
        c.gridx = 0;
        addParsingRules.add(Box.createHorizontalGlue(), c);

        c.gridheight = 3;
        c.gridx++;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        addParsingRules.add(plus, c);

        return addParsingRules;
    }

    private JPanel createParsingRuleFieldsPanel() {
        // creation du panel et du layout
        JPanel parsingRuleFields = new JPanel(new GridBagLayout());
        parsingRuleFields.setBorder(BorderFactory.createTitledBorder(""));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new java.awt.Insets(5, 5, 5, 5);

        // creation des widgets
        labelField = new JTextField();
        labelField.setPreferredSize(new Dimension(60, 20));

        accessionParseRuleField = new JTextField();
        accessionParseRuleField.setPreferredSize(new Dimension(60, 20));

        fastaPatternField = new JTextField();
        fastaPatternField.setPreferredSize(new Dimension(200, 20));

        fastaVersionField = new JTextField();
        fastaVersionField.setPreferredSize(new Dimension(200, 20));

        filler = Box.createHorizontalGlue();

        // ajout des widgets au layout
        c.gridx = 0;
        c.gridy = 0;
        parsingRuleFields.add(new JLabel("Label : "), c);

        c.gridx++;
        parsingRuleFields.add(new JLabel("Accession Parse Rule : "), c);

        c.gridy++;
        c.gridx = 0;
        parsingRuleFields.add(labelField, c);

        c.gridx++;
        parsingRuleFields.add(accessionParseRuleField, c);

        c.gridy++;
        c.gridx = 0;
        parsingRuleFields.add(new JLabel("Fasta Pattern : "), c);

        c.gridx++;
        parsingRuleFields.add(new JLabel("Fasta file : "), c);

        c.gridy++;
        c.gridx = 0;
        c.weightx = 1;
        parsingRuleFields.add(fastaPatternField, c);

        c.gridx++;
        parsingRuleFields.add(fastaVersionField, c);

        return parsingRuleFields;

    }
    private JPanel displaysetOfRules(){
        JPanel displayRules=new JPanel(new GridBagLayout());
        GridBagConstraints constraints=new GridBagConstraints();
        displayRules.setBorder(BorderFactory.createTitledBorder("List of parsingRules"));
        ArrayList<ParsingRule> setOfRules=ConfigManager.getInstance().getParsingRulesManager().getSetOfRules();
        constraints.gridy=0;
        constraints.gridx=0;
        for (int i=0;i< setOfRules.size();i++){
            ParsingRule toBedisplayed=setOfRules.get(i);
            String label=toBedisplayed.getName();
            String fastaVersion=toBedisplayed.getFasta_version();
            String protein=toBedisplayed.getProtein();
            JTextField labelField=new JTextField(label);
            JTextField fastaVersionField=new JTextField(fastaVersion);
            JTextField proteinField=new JTextField(protein);
            constraints.gridx=0;
            constraints.weightx=1;
            displayRules.add(labelField,constraints);
            constraints.weightx=1;
            constraints.gridx++;
            displayRules.add(fastaVersionField,constraints);
            constraints.weightx=1;
            constraints.gridx++;

            displayRules.add(proteinField,constraints);

            constraints.gridy++;
        }
        return  displayRules;
    }

    public void updateValues() {
        // TODO Auto-generated method stub

    }

    private ActionListener addRule() {
        ActionListener addParseRule = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (!labelField.getText().isEmpty() && !accessionParseRuleField.getText().isEmpty()
                        && !fastaPatternField.getText().isEmpty()) {
                    String addedLabel = labelField.getText();
                    String addedRegex = accessionParseRuleField.getText();
                    String addedFasta = fastaPatternField.getText();

                    ParsingRule pr = new ParsingRule(addedLabel,buildtestlist() , addedFasta, addedRegex);
                    boolean success= ConfigManager.getInstance().getParsingRulesManager().addNewRule(pr);
                    if (success){
                   // ConfigManager.getInstance().getParsingRulesManager().updateConfigFileParseRules();
                    updatePanel();
                        revalidate();
                        repaint();}
                }

            }
        };
        return addParseRule;
    }

/*    // TODO : à refaire avec un nouveau panel
    private ActionListener addParsingRule() {
        ActionListener addParseRule = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setLayout(new GridBagLayout());
                GridBagConstraints constraints = new GridBagConstraints();
                if (!labelField.getText().isEmpty() && !accessionParseRuleField.getText().isEmpty()
                        && !fastaPatternField.getText().isEmpty()) {

                    JLabel addedLabel = new JLabel(labelField.getText());
                    JLabel addedRegex = new JLabel(accessionParseRuleField.getText());
                    JLabel addedFasta = new JLabel(fastaPatternField.getText());

                    addParsingRules.remove(filler);
                    constraints.fill = GridBagConstraints.NONE;
                    constraints.weighty = 0;
                    constraints.gridx = 0;

                    addParsingRules.add(addedLabel, constraints);
                    constraints.gridx++;

                    addParsingRules.add(addedRegex, constraints);
                    constraints.gridx++;

                    addParsingRules.add(addedFasta, constraints);
                    constraints.gridx++;

                    // remove button
                    JButton delete = new JButton("x");
                    try {
                        Icon crossIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("cross.png")));
                        delete.setText("");
                        delete.setIcon(crossIcon);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    addParsingRules.add(delete, constraints);

                    ActionListener delParseRule = new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            addParsingRules.remove(addedLabel);
                            addParsingRules.remove(addedFasta);
                            addParsingRules.remove(addedRegex);
                            addParsingRules.remove(delete);
                            revalidate();
                            repaint();
                        }
                    };
                    delete.addActionListener(delParseRule);
                    constraints.gridy++;

                    labelField.setText("");
                    accessionParseRuleField.setText("");
                    fastaPatternField.setText("");

                    constraints.fill = GridBagConstraints.VERTICAL;
                    constraints.weighty = 1;
                    addParsingRules.add(filler, constraints);
                    revalidate();
                    repaint();
                }
            }
        };
        return addParseRule;
    }*/
}
