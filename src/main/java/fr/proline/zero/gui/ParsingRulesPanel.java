package fr.proline.zero.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ParsingRulesPanel extends JPanel {
	private JTextField labelField;
	private JTextField accessionParseRuleField;
	private JTextField fastaPatternField;
	private JTextField fastaVersionField;
	private Component filler;
	private JTextArea aide;
	private JPanel addParsingRules;
	private GridBagConstraints c;

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
		// TODO : texte à changer et centrer avec une icone
		aide = new JTextArea();
		aide.setMinimumSize(new Dimension(300, 75));
		aide.setPreferredSize(new Dimension(300, 75));
		aide.setText("ici est l'aide concernant l'onglet \nparsing rules");
		aide.setEditable(false);

		// ajout des widgets au layout
		c.gridx = 0;
		c.gridy = 0;
		add(aide, c);

		c.insets = new java.awt.Insets(20, 15, 0, 15);
		c.gridy++;
		add(createParsingRulesPanel(), c);

		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 1;
		add(Box.createHorizontalGlue(), c);
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

	public void updateValues() {
		// TODO Auto-generated method stub

	}

	// TODO : à refaire avec un nouveau panel
//	private ActionListener addParsingRule() {
//		ActionListener addParseRule = new ActionListener() {
//			public void actionPerformed(ActionEvent event) {
//				if (!labelField.getText().isEmpty() && !accessionParseRuleField.getText().isEmpty()
//						&& !fastaPatternField.getText().isEmpty()) {
//
//					JLabel addedLabel = new JLabel(labelField.getText());
//					JLabel addedRegex = new JLabel(accessionParseRuleField.getText());
//					JLabel addedFasta = new JLabel(fastaPatternField.getText());
//
//					addParsingRules.remove(filler);
//					addParsingRulesConstraints.fill = GridBagConstraints.NONE;
//					addParsingRulesConstraints.weighty = 0;
//					addParsingRulesConstraints.gridx = 0;
//
//					addParsingRules.add(addedLabel, addParsingRulesConstraints);
//					addParsingRulesConstraints.gridx++;
//
//					addParsingRules.add(addedRegex, addParsingRulesConstraints);
//					addParsingRulesConstraints.gridx++;
//
//					addParsingRules.add(addedFasta, addParsingRulesConstraints);
//					addParsingRulesConstraints.gridx++;
//
//					// remove button
//					JButton delete = new JButton("x");
//					try {
//						Icon crossIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("cross.png")));
//						delete.setText("");
//						delete.setIcon(crossIcon);
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//					addParsingRules.add(delete, addParsingRulesConstraints);
//
//					ActionListener delParseRule = new ActionListener() {
//						public void actionPerformed(ActionEvent e) {
//							addParsingRules.remove(addedLabel);
//							addParsingRules.remove(addedFasta);
//							addParsingRules.remove(addedRegex);
//							addParsingRules.remove(delete);
//							revalidate();
//							repaint();
//						}
//					};
//					delete.addActionListener(delParseRule);
//					addParsingRulesConstraints.gridy++;
//
//					labelField.setText("");
//					accessionParseRuleField.setText("");
//					fastaPatternField.setText("");
//
//					addParsingRulesConstraints.fill = GridBagConstraints.VERTICAL;
//					addParsingRulesConstraints.weighty = 1;
//					addParsingRules.add(filler, addParsingRulesConstraints);
//					revalidate();
//					repaint();
//				}
//			}
//		};
//		return addParseRule;
//	}
}
