package fr.proline.zero.gui;

import java.awt.*;
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
    private JPanel addParsingRules;
    private JPanel parsingRuleFields;

    private GridBagConstraints c;

    private ArrayList<String> fastaList;
    private JTextField fastaAdded;
    private GridBagConstraints parsingConstraints;

    private boolean noPriorAdd = true;

    private JList<String> jList;

    public ArrayList<String> listForTests() {
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
        c.anchor=GridBagConstraints.FIRST_LINE_START;
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
        parsingRuleFields = new JPanel(new GridBagLayout());
        parsingRuleFields.setBorder(BorderFactory.createTitledBorder(""));
        parsingConstraints = new GridBagConstraints();
        parsingConstraints.fill = GridBagConstraints.HORIZONTAL;
        parsingConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        parsingConstraints.insets = new Insets(5, 5, 5, 5);

        // creation des widgets
        labelField = new JTextField();
      //  labelField.setPreferredSize(new Dimension(60, 20));

        accessionParseRuleField = new JTextField();
       // accessionParseRuleField.setPreferredSize(new Dimension(60, 20));

        fastaPatternField = new JTextField();
       // fastaPatternField.setPreferredSize(new Dimension(200, 20));
        fastaList = new ArrayList<>();


        // ajout des widgets au layout
        parsingConstraints.gridx = 0;
        parsingConstraints.gridy = 0;
      //  parsingConstraints.anchor=GridBagConstraints.FIRST_LINE_START;
        parsingRuleFields.add(new JLabel("Label : "), parsingConstraints);

        parsingConstraints.gridx++;
      //  parsingConstraints.anchor=GridBagConstraints.CENTER;
        parsingRuleFields.add(new JLabel("Accession Parse Rule : "), parsingConstraints);

        parsingConstraints.gridy++;
        parsingConstraints.gridx = 0;
        parsingRuleFields.add(labelField, parsingConstraints);

        parsingConstraints.gridx++;
        parsingRuleFields.add(accessionParseRuleField, parsingConstraints);


        parsingConstraints.gridy = 0;
        parsingConstraints.gridx++;
        parsingRuleFields.add(new JLabel("Fasta Pattern : "), parsingConstraints);

        parsingConstraints.gridy++;

        parsingRuleFields.add(fastaPatternField, parsingConstraints);
        JLabel fastaLabel = new JLabel("fasta-names:  ");
        parsingConstraints.gridx = 0;
        parsingConstraints.gridy++;
        parsingConstraints.anchor=GridBagConstraints.SOUTHWEST;
        parsingRuleFields.add(fastaLabel, parsingConstraints);

        parsingConstraints.gridx = 0;
        parsingConstraints.gridy++;

        fastaAdded = new JTextField();
        //labels = new ArrayList<>();

        fastaAdded.setPreferredSize(new Dimension(50, 30));
        parsingRuleFields.add(fastaAdded, parsingConstraints);
        //String fasta = fastaAdded.getText();
        JButton addButton = new JButton("add");
        try {

            Icon addIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("ajouter.png")));
            addButton = new JButton(addIcon);
            addButton.addActionListener(addFastaName());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        parsingConstraints.gridx++;
        parsingConstraints.weightx=0;
        parsingConstraints.anchor = GridBagConstraints.SOUTHWEST;
        parsingRuleFields.add(addButton, parsingConstraints);
        parsingConstraints.gridx++;


        // private JTextField fastaVersionField;
        Component filler = Box.createHorizontalGlue();

        return parsingRuleFields;

    }


    private JPanel displaysetOfRules() {
        JPanel displayRules = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        displayRules.setBorder(BorderFactory.createTitledBorder("List of parsingRules"));
        ArrayList<ParsingRule> setOfRules = ConfigManager.getInstance().getParsingRulesManager().getSetOfRules();
        constraints.gridy = 0;
        constraints.gridx = 0;
        Icon deleteIcon ;
        try {
            deleteIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("cross.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < setOfRules.size(); i++) {
            ParsingRule toBedisplayed = setOfRules.get(i);
            String label = toBedisplayed.getName();
            String fastaVersion = toBedisplayed.getFasta_version();
            String protein = toBedisplayed.getProtein();
            JLabel labelField = new JLabel(label);
            JTextField fastaVersionField = new JTextField(fastaVersion);
            JTextField proteinField = new JTextField(protein);
            constraints.gridx = 0;
            constraints.weightx = 0.2;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.fill = GridBagConstraints.NONE;

            displayRules.add(labelField, constraints);
            constraints.weightx = 1;
            constraints.gridx++;
            constraints.fill = GridBagConstraints.HORIZONTAL;

            fastaVersionField.setPreferredSize(new Dimension(150, 20));
            displayRules.add(fastaVersionField, constraints);
            constraints.weightx = 1;
            constraints.gridx++;
            constraints.fill = GridBagConstraints.HORIZONTAL;

            proteinField.setPreferredSize(new Dimension(150, 20));
            displayRules.add(proteinField, constraints);
            constraints.gridx++;
            JButton deleteButton = new JButton(deleteIcon);
            deleteButton.setHorizontalAlignment(SwingConstants.CENTER);
            deleteButton.setToolTipText("Click to delete the parsing rule");
            deleteButton.addActionListener(dellRule(toBedisplayed));
            constraints.anchor = GridBagConstraints.EAST;
            constraints.fill = GridBagConstraints.NONE;
            displayRules.add(deleteButton, constraints);

            constraints.gridy++;
        }
        return displayRules;
    }

    public void updateValues() {
        // TODO Auto-generated method stub
        updatePanel();
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
                    ArrayList<String> list = fastaList;

                    ParsingRule pr = new ParsingRule(addedLabel, list, addedFasta, addedRegex);
                    boolean success = ConfigManager.getInstance().getParsingRulesManager().addNewRule(pr);
                    if (success) {
                        updatePanel();
                    }
                }

            }
        };
        return addParseRule;
    }

    private ActionListener dellRule(ParsingRule parsingRuleToBeDeleted) {
        ActionListener dellParseRule = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean success = ConfigManager.getInstance().getParsingRulesManager().dellRule(parsingRuleToBeDeleted);
                if (success) {
                    updatePanel();
                }

            }
        };
        return dellParseRule;
    }

    private ActionListener addFastaName() {
        ActionListener fastaListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                String fastaName = fastaAdded.getText();
                if (fastaName.length() != 0) {
                    fastaList.add(fastaName);
                    fastaAdded.setText("");
                    parsingConstraints.gridx++;

                    String[] fastaStringArray = new String[fastaList.size()];
                    for (int i = 0; i < fastaList.size(); i++) {
                        fastaStringArray[i] = fastaList.get(i);
                    }

                    if (noPriorAdd) {
                        jList = new JList<>(fastaStringArray);
                        jList.setBorder(BorderFactory.createTitledBorder("fasta-names"));
                        jList.setSize(100, 100);
                        parsingRuleFields.add(jList, parsingConstraints);
                        repaint();
                        revalidate();
                        noPriorAdd = false;
                    } else {
                        parsingRuleFields.remove(jList);
                        jList = new JList<>(fastaStringArray);
                        jList.setBorder(BorderFactory.createTitledBorder("fasta-names"));
                        jList.setSize(100, 100);
                        parsingRuleFields.add(jList, parsingConstraints);
                        repaint();
                        revalidate();
                    }


                }
                else {
                    Popup.warning("you must enter a value");
                }
            }


        };
        return fastaListener;
    }


}
